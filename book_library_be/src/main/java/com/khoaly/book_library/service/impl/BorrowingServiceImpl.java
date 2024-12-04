package com.khoaly.book_library.service.impl;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.dto.BorrowBookHistoryDetailDto;
import com.khoaly.book_library.dto.BorrowBookHistoryDto;
import com.khoaly.book_library.dto.BorrowingDetailDto;
import com.khoaly.book_library.dto.BorrowingDto;
import com.khoaly.book_library.dto.ExtendBorrowingDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.dto.ReturnBorrowingDto;
import com.khoaly.book_library.dto.ReturnDetailDto;
import com.khoaly.book_library.entity.Account;
import com.khoaly.book_library.entity.Book;
import com.khoaly.book_library.entity.BookInventory;
import com.khoaly.book_library.entity.Borrowing;
import com.khoaly.book_library.entity.BorrowingDetail;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.BookStatusEnum;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import com.khoaly.book_library.exception.InvalidBookToBorrowException;
import com.khoaly.book_library.exception.InvalidAccountBorrowingException;
import com.khoaly.book_library.exception.InvalidBorrowPeriodExtensionException;
import com.khoaly.book_library.exception.MaximumBorrowLimitException;
import com.khoaly.book_library.exception.MaximumBorrowPeriodException;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.notification.Notification;
import com.khoaly.book_library.notification.NotificationService;
import com.khoaly.book_library.repository.AccountRepository;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.repository.BorrowingRepository;
import com.khoaly.book_library.service.BorrowingService;
import com.khoaly.book_library.specification.BorrowingSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BorrowingServiceImpl implements BorrowingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BorrowingServiceImpl.class);
    private final BorrowingRepository borrowingRepository;
    private final AccountRepository accountRepository;
    private final BookRepository bookRepository;
    private final NotificationService notificationService;
    private final ModelMapper modelMapper;

    /**
     * Create new borrowing flow
     * Step 1: Check account status
     * Step 2: Validate borrowing period, total borrow book amount
     * Step 3: For each book borrow, check book borrow quantity and status
     * Step 4: Insert new borrowing record
     */
    @Override
    public void createBorrowing(BorrowingDto borrowingDto) {
        Account borrowAccount = accountRepository
                .findAccountByIdAndStatusEquals(borrowingDto.getAccountBorrowId(), AccountStatusEnum.ACTIVE)
                .orElseThrow(() -> new InvalidAccountBorrowingException("This account not found or can not borrow book"));

        int totalBorrowBook = borrowingDto.getBookList().stream().mapToInt(BorrowingDetailDto::getBorrowAmount).sum();
        if (totalBorrowBook > CommonConstant.MAXIMUM_BORROW_LIMIT) {
            throw new MaximumBorrowLimitException("Cannot borrow more than 3 books at a time");
        }

        long borrowingPeriod = ChronoUnit.DAYS.between(borrowingDto.getBorrowingDate(), borrowingDto.getDueDate());
        if (borrowingPeriod > CommonConstant.MAXIMUM_BORROW_PERIOD) {
            throw new MaximumBorrowPeriodException("The borrowing period cannot exceed 3 days");
        }

        if (!this.validateBorrowBook(borrowingDto.getBookList())) {
            Borrowing borrowing = Borrowing
                    .builder()
                    .memberBorrow(borrowAccount.getMember())
                    .borrowingDate(LocalDate.now())
                    .totalBook(totalBorrowBook)
                    .dueDate(borrowingDto.getDueDate())
                    .status(BorrowingStatusEnum.BORROWED)
                    .build();

            List<BorrowingDetail> borrowingDetailList = borrowingDto.getBookList()
                    .stream()
                    .map(borrowingDetailDto -> {
                        Optional<Book> borrowingBook = bookRepository.findById(borrowingDetailDto.getBookId());
                        return borrowingBook.map(book -> BorrowingDetail
                                        .builder()
                                        .borrowing(borrowing)
                                        .book(borrowingBook.get())
                                        .borrowAmount(borrowingDetailDto.getBorrowAmount())
                                        .returnAmount(NumberUtils.INTEGER_ZERO)
                                        .build())
                                .orElse(null);
                    })
                    .filter(Objects::nonNull)
                    .toList();
            Set<BorrowingDetail> borrowingDetailSet = new HashSet<>(borrowingDetailList);
            borrowing.setBorrowingDetails(borrowingDetailSet);

            borrowingRepository.save(borrowing);

            updateBookInventoryForBorrowingBook(borrowingDto.getBookList());
        } else {
            LOGGER.info("Book list for borrowing includes one or more book which can not borrow");

            throw new InvalidBookToBorrowException("Book list for borrowing includes one or more book which can not borrow");
        }
    }

    /**
     * Return borrowing book flow
     * Step 1: Find current borrowing
     * Step 2: Update return date, return amount, status borrowing
     * Step 3: Update book inventory base on return book's quantity
     * Step 4: Update status book (OUT_OF_STOCK -> AVAILABLE) (case current borrow book has status OUT_OF_STOCK)
     */
    @Override
    public void returnBorrowing(ReturnBorrowingDto returnBorrowingDto) {
        //Step 1
        Borrowing currentBorrowing = this.checkBorrowingExist(returnBorrowingDto.getBorrowingId());

        //Step 2
        currentBorrowing.setReturnDate(LocalDate.now());

        if (currentBorrowing.getStatus().equals(BorrowingStatusEnum.BORROWED)) {
            for (ReturnDetailDto returnDetailDto : returnBorrowingDto.getBookList()) {
                currentBorrowing.getBorrowingDetails().stream()
                        .filter(borrowingDetail -> borrowingDetail.getBook().getId().equals(returnDetailDto.getBookId()))
                        .forEach(borrowingDetail -> borrowingDetail.setReturnAmount(returnDetailDto.getReturnAmount()));
            }
        }

        boolean isReturnAllBook = currentBorrowing.getBorrowingDetails().stream()
                .allMatch(borrowingDetail -> Objects.equals(borrowingDetail.getBorrowAmount(), borrowingDetail.getReturnAmount()));
        if (isReturnAllBook) {
            currentBorrowing.setStatus(BorrowingStatusEnum.RETURNED);
        }

        //Step 3 + 4
        updateBookInventoryForReturningBook(returnBorrowingDto.getBookList());
    }

    @Override
    public void extendBorrowing(ExtendBorrowingDto extendBorrowingDto) {
        Borrowing currentBorrowing = this.checkBorrowingExist(extendBorrowingDto.getBorrowingId());

        long newBorrowingPeriod = ChronoUnit.DAYS.between(currentBorrowing.getDueDate(), extendBorrowingDto.getNewDueDate());
        if (newBorrowingPeriod <= NumberUtils.INTEGER_ZERO || newBorrowingPeriod > CommonConstant.MAXIMUM_BORROW_PERIOD) {
            throw new InvalidBorrowPeriodExtensionException("The extension exceeds the maximum allowable period of 3 days or is not a valid extension date");
        }

        currentBorrowing.setDueDate(extendBorrowingDto.getNewDueDate());
        borrowingRepository.save(currentBorrowing);
    }

    @Override
    public void cancelBorrowing(int borrowingId) {
        Borrowing currentBorrowing = this.checkBorrowingExist(borrowingId);

        if (currentBorrowing.getStatus().equals(BorrowingStatusEnum.BORROWED)) {
            currentBorrowing.setStatus(BorrowingStatusEnum.CANCELLED);
            borrowingRepository.save(currentBorrowing);

            LOGGER.info("Cancel borrowing id {} successfully", borrowingId);
        }
    }

    @Override
    public PageResponse<BorrowBookHistoryDto> getBorrowBookHistory(int pageNo, LocalDate startDate, LocalDate endDate, BorrowingStatusEnum borrowingStatusEnum) {
        Pageable pageable = PageRequest
                .of(pageNo, CommonConstant.PAGE_SIZE, Sort.by(CommonConstant.BORROW_SORT_BY).descending());

        Specification<Borrowing> borrowingSpecification = Specification
                .where(BorrowingSpecification.hasStatus(borrowingStatusEnum))
                .and(BorrowingSpecification.borrowingDateBetween(startDate, endDate));

        Page<Borrowing> pageData = borrowingRepository.findAll(borrowingSpecification, pageable);

        List<BorrowBookHistoryDto> borrowBookHistoryDtoList = pageData
                .stream()
                .map(borrowing -> modelMapper.map(borrowing, BorrowBookHistoryDto.class))
                .toList();

        return PageResponse.<BorrowBookHistoryDto>builder()
                .currentPage(pageNo)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(borrowBookHistoryDtoList)
                .build();
    }

    @Override
    public List<BorrowBookHistoryDetailDto> getBorrowBookHistoryDetail(int borrowingId) {
        Borrowing currentBorrowing = this.checkBorrowingExist(borrowingId);

        return currentBorrowing.getBorrowingDetails()
                .stream()
                .map(borrowingDetail -> modelMapper.map(borrowingDetail, BorrowBookHistoryDetailDto.class))
                .toList();
    }

    @Override
    public PageResponse<BorrowBookHistoryDto> getBorrowBookHistoryForReader(int accountBorrowId, int pageNo, BorrowingStatusEnum borrowingStatusEnum) {
        Account borrowedAccount = accountRepository.findById(accountBorrowId).orElseThrow(() -> new NotFoundException("Account not found"));

        Pageable pageable = PageRequest.of(pageNo, CommonConstant.PAGE_SIZE, Sort.by(CommonConstant.BORROW_SORT_BY).descending());

        Page<Borrowing> pageData = borrowingRepository.findByMemberBorrow_IdAndOptionalStatus(
                borrowedAccount.getId(), borrowingStatusEnum, pageable);

        List<BorrowBookHistoryDto> borrowBookHistoryDtoList = pageData
                .stream()
                .map(borrowing -> modelMapper.map(borrowing, BorrowBookHistoryDto.class))
                .toList();

        return PageResponse.<BorrowBookHistoryDto>builder()
                .currentPage(pageNo)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(borrowBookHistoryDtoList)
                .build();
    }

    @Override
    public void scheduleOverdueBorrowing() {
        LocalDate dueDate = LocalDate.now();
        List<Borrowing> borrowingList = borrowingRepository.findAllByDueDate(dueDate);

        if (!CollectionUtils.isEmpty(borrowingList)) {
            List<Borrowing> overdueBorrowing = borrowingList.stream()
                    .filter(borrowing -> borrowing.getStatus().equals(BorrowingStatusEnum.BORROWED))
                    .map(borrowing -> {
                        borrowing.setStatus(BorrowingStatusEnum.OVERDUE);
                        return borrowing;
                    })
                    .toList();

            LOGGER.info("There are {} borrowing overdue in {}", overdueBorrowing.size(), dueDate);

            notificationService.sendNotification(Notification
                    .builder()
                    .message("There are " + overdueBorrowing.size() + " borrowing overdue in " + dueDate)
                    .build());

            borrowingRepository.saveAll(overdueBorrowing);
        } else {
            LOGGER.info("There is no borrowing overdue in {}", dueDate);

            notificationService.sendNotification(Notification
                    .builder()
                    .message("There is no borrowing overdue in " + dueDate)
                    .build());
        }
    }

    private Borrowing checkBorrowingExist(int borrowingId) {
        return borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new NotFoundException("No borrowing found"));
    }

    private void updateBookInventoryForReturningBook(List<ReturnDetailDto> returnDetailDtoList) {
        for (ReturnDetailDto returnDetailDto : returnDetailDtoList) {
            Optional<Book> bookOptional = bookRepository.findById(returnDetailDto.getBookId());
            if (bookOptional.isPresent()) {
                Book currentBook = bookOptional.get();
                BookInventory bookInventory = currentBook.getBookInventory();
                bookInventory.setCurrentQuantity(bookInventory.getCurrentQuantity() + returnDetailDto.getReturnAmount());

                if (bookInventory.getCurrentQuantity() > bookInventory.getTotalQuantity()) {
                    bookInventory.setCurrentQuantity(bookInventory.getTotalQuantity());
                }

                if (currentBook.getStatus().equals(BookStatusEnum.OUT_OF_STOCK) && bookInventory.getCurrentQuantity() > NumberUtils.INTEGER_ZERO) {
                    currentBook.setStatus(BookStatusEnum.AVAILABLE);
                }

                bookRepository.save(currentBook);
            }
        }
    }

    private void updateBookInventoryForBorrowingBook(List<BorrowingDetailDto> borrowingDetailDtoList) {
        for (BorrowingDetailDto borrowingDetailDto : borrowingDetailDtoList) {
            Optional<Book> bookOptional = bookRepository.findById(borrowingDetailDto.getBookId());
            if (bookOptional.isPresent()) {
                Book currentBorrowingBook = bookOptional.get();
                BookInventory bookInventory = currentBorrowingBook.getBookInventory();
                bookInventory.setCurrentQuantity(bookInventory.getCurrentQuantity() - borrowingDetailDto.getBorrowAmount());

                if (Objects.equals(bookInventory.getCurrentQuantity(), NumberUtils.INTEGER_ZERO)) {
                    currentBorrowingBook.setStatus(BookStatusEnum.OUT_OF_STOCK);
                }

                bookRepository.save(currentBorrowingBook);
            }
        }
    }

    private boolean validateBorrowBook(List<BorrowingDetailDto> borrowingDetailDtoList) {
        for (BorrowingDetailDto borrowingDetailDto : borrowingDetailDtoList) {
            int bookId = borrowingDetailDto.getBookId();
            int amountToBorrow = borrowingDetailDto.getBorrowAmount();

            Optional<Book> bookOptional = bookRepository.findById(bookId);
            if (bookOptional.isPresent()) {
                BookInventory bookInventory = bookOptional.get().getBookInventory();

                if (amountToBorrow > bookInventory.getCurrentQuantity()) {
                    return Boolean.TRUE;
                }
            } else {
                return Boolean.FALSE;
            }
        }

        return Boolean.FALSE;
    }
}
