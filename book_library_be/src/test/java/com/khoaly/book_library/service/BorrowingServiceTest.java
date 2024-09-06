package com.khoaly.book_library.service;

import com.khoaly.book_library.constant.CommonConstant;
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
import com.khoaly.book_library.entity.Member;
import com.khoaly.book_library.enumeration.AccountRoleEnum;
import com.khoaly.book_library.enumeration.AccountStatusEnum;
import com.khoaly.book_library.enumeration.BookStatusEnum;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import com.khoaly.book_library.enumeration.MembershipTypeEnum;
import com.khoaly.book_library.exception.InvalidBookToBorrowException;
import com.khoaly.book_library.exception.InvalidAccountBorrowingException;
import com.khoaly.book_library.exception.InvalidBorrowPeriodExtensionException;
import com.khoaly.book_library.exception.MaximumBorrowLimitException;
import com.khoaly.book_library.exception.MaximumBorrowPeriodException;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.repository.AccountRepository;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.repository.BorrowingRepository;
import com.khoaly.book_library.service.impl.BorrowingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
class BorrowingServiceTest {

    @Autowired
    private BorrowingServiceImpl borrowingService;

    @MockBean
    private BorrowingRepository borrowingRepository;

    @MockBean
    private AccountRepository accountRepository;

    @MockBean
    private BookRepository bookRepository;

    private Member member;
    private Account account;
    private Book book;
    private BorrowingDto borrowingDto;

    @BeforeEach
    void initData() {
        //Member
        LocalDate membershipStartDate = LocalDate.of(2024, 8, 19);
        LocalDate membershipEndDate = LocalDate.of(2025, 8, 19);
        member = Member
                .builder()
                .id(1)
                .currentBorrowedBooks(0)
                .membershipEndDate(membershipEndDate)
                .membershipNumber("MBUrsola202408197190")
                .membershipStartDate(membershipStartDate)
                .membershipType(MembershipTypeEnum.BASIC)
                .totalBooksBorrowed(0)
                .totalLateReturns(0)
                .build();
        account = Account.builder()
                .id(1)
                .address("123 Main St, Anytown, USA")
                .email("upurdy0@cdbaby.com")
                .firstName("Ursola")
                .lastName("Purdy")
                .notes(null)
                .phoneNumber("1234567890")
                .status(AccountStatusEnum.ACTIVE)
                .role(AccountRoleEnum.MEMBER)
                .member(member)
                .build();

        //Book
        book = Book.builder()
                .id(1)
                .averageRating(4.42)
                .numPages(352)
                .publicationDate(LocalDate.of(2003, 11, 1))
                .isbn("439554896")
                .isbn13("9780439554893")
                .title("Harry Potter and the Chamber of Secrets (Harry Potter  #2)")
                .authors("J.K. Rowling")
                .languageCode("eng")
                .publisher("Scholastic")
                .status(BookStatusEnum.AVAILABLE)
                .build();
        BookInventory bookInventory = BookInventory.builder()
                .book(book)
                .currentQuantity(1)
                .totalQuantity(10)
                .build();
        book.setBookInventory(bookInventory);

        //BorrowingDto
        borrowingDto = new BorrowingDto();
        borrowingDto.setAccountBorrowId(account.getId());
        borrowingDto.setBorrowingDate(LocalDate.now());
        borrowingDto.setDueDate(LocalDate.now().plusDays(3));

        BorrowingDetailDto borrowingDetailDto1 = new BorrowingDetailDto(1, 1);
        BorrowingDetailDto borrowingDetailDto2 = new BorrowingDetailDto(2, 1);
        List<BorrowingDetailDto> bookList = List.of(borrowingDetailDto1, borrowingDetailDto2);
        borrowingDto.setBookList(bookList);
    }

    @Test
    void createBorrowing_ShouldReturnSuccess() {
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(accountRepository.findAccountByIdAndStatusEquals(anyInt(), any())).thenReturn(Optional.of(account));

        borrowingService.createBorrowing(borrowingDto);

        ArgumentCaptor<Borrowing> borrowingCaptor = ArgumentCaptor.forClass(Borrowing.class);
        verify(borrowingRepository, times(1)).save(borrowingCaptor.capture());

        Borrowing capturedBorrowing = borrowingCaptor.getValue();
        assertEquals(member, capturedBorrowing.getMemberBorrow());
        assertEquals(2, capturedBorrowing.getTotalBook());
        assertEquals(borrowingDto.getDueDate(), capturedBorrowing.getDueDate());
        assertEquals(BorrowingStatusEnum.BORROWED, capturedBorrowing.getStatus());
        assertEquals(1, capturedBorrowing.getBorrowingDetails().iterator().next().getBorrowAmount());
    }

    @Test
    void createBorrowing_InvalidAccount_ShouldReturnFail() {
        when(accountRepository.findAccountByIdAndStatusEquals(anyInt(), any())).thenReturn(Optional.empty());

        var exception = assertThrows(InvalidAccountBorrowingException.class, () -> borrowingService.createBorrowing(borrowingDto));

        assertEquals("This account not found or can not borrow book", exception.getMessage());
    }

    @Test
    void createBorrowing_ExceedsMaxBorrowLimit_ShouldReturnFail() {
        when(accountRepository.findAccountByIdAndStatusEquals(anyInt(), any())).thenReturn(Optional.of(account));

        borrowingDto.getBookList().forEach(borrowingDetailDto -> borrowingDetailDto.setBorrowAmount(3));

        var exception = assertThrows(MaximumBorrowLimitException.class, () -> borrowingService.createBorrowing(borrowingDto));

        assertEquals("Cannot borrow more than 3 books at a time", exception.getMessage());
    }

    @Test
    void createBorrowing_ExceedsMaxBorrowPeriod_ShouldReturnFail() {
        when(accountRepository.findAccountByIdAndStatusEquals(anyInt(), any())).thenReturn(Optional.of(account));

        borrowingDto.setDueDate(borrowingDto.getBorrowingDate().plusDays(4));
        var exception = assertThrows(MaximumBorrowPeriodException.class, () -> borrowingService.createBorrowing(borrowingDto));

        assertEquals("The borrowing period cannot exceed 3 days", exception.getMessage());
    }

    @Test
    void createBorrowing_InvalidBorrowBook_ShouldReturnFail() {
        book.getBookInventory().setCurrentQuantity(0);
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));
        when(accountRepository.findAccountByIdAndStatusEquals(anyInt(), any())).thenReturn(Optional.of(account));

        var exception = assertThrows(InvalidBookToBorrowException.class, () -> borrowingService.createBorrowing(borrowingDto));

        assertEquals("Book list for borrowing includes one or more book which can not borrow", exception.getMessage());
    }

    @Test
    void returnBorrowing_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();
        ReturnBorrowingDto returnBorrowingDto = this.initReturnBorrowingDto();
        int currentQuantityBeforeReturnBook = book.getBookInventory().getCurrentQuantity();

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));

        borrowingService.returnBorrowing(returnBorrowingDto);

        assertEquals(BorrowingStatusEnum.RETURNED, borrowing.getStatus());
        assertEquals(LocalDate.now(), borrowing.getReturnDate());
        assertEquals(book.getBookInventory().getCurrentQuantity(), currentQuantityBeforeReturnBook + returnBorrowingDto.getBookList().get(0).getReturnAmount());
    }

    @Test
    void returnBorrowing__UpdateBookStatus_ShouldReturnSuccess() {
        this.book.setStatus(BookStatusEnum.OUT_OF_STOCK);
        BookInventory bookInventory = BookInventory.builder()
                .book(book)
                .currentQuantity(0)
                .totalQuantity(10)
                .build();
        this.book.setBookInventory(bookInventory);

        Borrowing borrowing = this.initBorrowing();
        ReturnBorrowingDto returnBorrowingDto = this.initReturnBorrowingDto();
        int currentQuantityBeforeReturnBook = book.getBookInventory().getCurrentQuantity();

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));
        when(bookRepository.findById(anyInt())).thenReturn(Optional.of(book));

        borrowingService.returnBorrowing(returnBorrowingDto);

        assertEquals(BorrowingStatusEnum.RETURNED, borrowing.getStatus());
        assertEquals(LocalDate.now(), borrowing.getReturnDate());
        assertEquals(book.getBookInventory().getCurrentQuantity(), currentQuantityBeforeReturnBook + returnBorrowingDto.getBookList().get(0).getReturnAmount());
        assertEquals(BookStatusEnum.AVAILABLE, book.getStatus());
    }

    @Test
    void returnBorrowing_BorrowingNotFound_ShouldReturnFail() {
        ReturnBorrowingDto returnBorrowingDto = this.initReturnBorrowingDto();

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> borrowingService.returnBorrowing(returnBorrowingDto));

        assertEquals("No borrowing found", exception.getMessage());
    }

    @Test
    void extendBorrowing_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        ExtendBorrowingDto extendBorrowingDto = new ExtendBorrowingDto();
        extendBorrowingDto.setBorrowingId(1);
        extendBorrowingDto.setNewDueDate(LocalDate.now().plusDays(4));

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));

        borrowingService.extendBorrowing(extendBorrowingDto);

        assertEquals(LocalDate.now().plusDays(4), borrowing.getDueDate());
    }

    @Test
    void extendBorrowing_InvalidBorrowPeriodExtension_ShouldReturnFail() {
        Borrowing borrowing = this.initBorrowing();

        ExtendBorrowingDto extendBorrowingDto = new ExtendBorrowingDto();
        extendBorrowingDto.setBorrowingId(1);
        extendBorrowingDto.setNewDueDate(LocalDate.now().plusDays(10));

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));

        var exception = assertThrows(InvalidBorrowPeriodExtensionException.class, () -> borrowingService.extendBorrowing(extendBorrowingDto));

        assertEquals("The extension exceeds the maximum allowable period of 3 days or is not a valid extension date", exception.getMessage());
    }

    @Test
    void extendBorrowing_BorrowingNotFound_ShouldReturnFail() {
        ExtendBorrowingDto extendBorrowingDto = new ExtendBorrowingDto();
        extendBorrowingDto.setBorrowingId(1);
        extendBorrowingDto.setNewDueDate(LocalDate.now().plusDays(4));

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> borrowingService.extendBorrowing(extendBorrowingDto));

        assertEquals("No borrowing found", exception.getMessage());
    }

    @Test
    void cancelBorrowing_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));

        borrowingService.cancelBorrowing(1);

        assertEquals(BorrowingStatusEnum.CANCELLED, borrowing.getStatus());
    }

    @Test
    void getBorrowBookHistory_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        int pageNo = 0;
        int pageSize = CommonConstant.PAGE_SIZE;
        List<Borrowing> borrowings = List.of(borrowing);
        Page<Borrowing> pageData = new PageImpl<>(borrowings, PageRequest.of(pageNo, pageSize, Sort.by(CommonConstant.MEMBER_SORT_BY).descending()), borrowings.size());

        when(borrowingRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageData);

        LocalDate startDate = LocalDate.now().minusMonths(1);
        LocalDate endDate = LocalDate.now();
        PageResponse<BorrowBookHistoryDto> result = borrowingService.getBorrowBookHistory(pageNo, startDate, endDate, BorrowingStatusEnum.BORROWED);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BorrowingStatusEnum.BORROWED, result.getData().get(0).getStatus());
    }

    @Test
    void getBorrowBookHistoryDetail_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        when(borrowingRepository.findById(anyInt())).thenReturn(Optional.of(borrowing));

        var response = borrowingService.getBorrowBookHistoryDetail(1);

        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void getBorrowBookHistoryForReader_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        int pageNo = 0;
        int pageSize = CommonConstant.PAGE_SIZE;
        List<Borrowing> borrowings = List.of(borrowing);
        Page<Borrowing> pageData = new PageImpl<>(borrowings, PageRequest.of(pageNo, pageSize, Sort.by(CommonConstant.MEMBER_SORT_BY).descending()), borrowings.size());

        when(accountRepository.findById(anyInt())).thenReturn(Optional.of(account));
        when(borrowingRepository.findByMemberBorrow_IdAndOptionalStatus(anyInt(), any(BorrowingStatusEnum.class), any(Pageable.class))).thenReturn(pageData);

        PageResponse<BorrowBookHistoryDto> result = borrowingService.getBorrowBookHistoryForReader(member.getId(), pageNo, BorrowingStatusEnum.BORROWED);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(BorrowingStatusEnum.BORROWED, result.getData().get(0).getStatus());
        assertEquals(member.getMembershipNumber(), result.getData().get(0).getMemberMembershipNumber());
    }

    @Test
    void scheduleOverdueBorrowing_ShouldReturnSuccess() {
        Borrowing borrowing = this.initBorrowing();

        when(borrowingRepository.findAllByDueDate(LocalDate.now())).thenReturn(List.of(borrowing));

        borrowingService.scheduleOverdueBorrowing();

        assertEquals(BorrowingStatusEnum.OVERDUE, borrowing.getStatus());
    }

    private ReturnBorrowingDto initReturnBorrowingDto() {
        ReturnBorrowingDto returnBorrowingDto = new ReturnBorrowingDto();
        returnBorrowingDto.setBorrowingId(1);

        ReturnDetailDto returnDetailDto = new ReturnDetailDto();
        returnDetailDto.setBookId(book.getId());
        returnDetailDto.setReturnAmount(2);
        List<ReturnDetailDto> returnDetailDtoList = List.of(returnDetailDto);
        returnBorrowingDto.setBookList(returnDetailDtoList);

        return returnBorrowingDto;
    }

    private Borrowing initBorrowing() {
        BorrowingDetail borrowingDetail = BorrowingDetail
                .builder()
                .book(book)
                .borrowAmount(2)
                .returnAmount(0)
                .build();

        List<BorrowingDetail> borrowingDetailList = List.of(borrowingDetail);
        Borrowing borrowing = Borrowing.builder().build();
        Set<BorrowingDetail> borrowingDetailSet = new HashSet<>(borrowingDetailList);

        borrowing.setId(1);
        borrowing.setBorrowingDetails(borrowingDetailSet);
        borrowing.setStatus(BorrowingStatusEnum.BORROWED);
        borrowing.setMemberBorrow(member);
        borrowing.setBorrowingDate(LocalDate.now().minusDays(1));
        borrowing.setDueDate(LocalDate.now().plusDays(2));
        borrowing.setTotalBook(2);

        return borrowing;
    }
}
