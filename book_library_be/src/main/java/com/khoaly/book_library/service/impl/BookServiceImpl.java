package com.khoaly.book_library.service.impl;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.dto.BookDetailDto;
import com.khoaly.book_library.dto.BookDto;
import com.khoaly.book_library.dto.BookImportDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.entity.Book;
import com.khoaly.book_library.entity.BookInventory;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.service.BookService;
import com.khoaly.book_library.specification.BookSpecification;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookServiceImpl.class);
    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    @Override
    public PageResponse<BookDto> getBooks(int pageNo, String title) {
        Pageable pageable = PageRequest
                .of(pageNo, CommonConstant.PAGE_SIZE, Sort.by(CommonConstant.BOOK_SORT_BY).descending());
        Page<Book> pageData = bookRepository.findAll(Specification.where(BookSpecification.titleLike(title)), pageable);

        return PageResponse.<BookDto>builder()
                .currentPage(pageNo)
                .pageSize(pageData.getSize())
                .totalPages(pageData.getTotalPages())
                .totalElements(pageData.getTotalElements())
                .data(pageData.getContent()
                        .stream()
                        .map(element -> {
                            BookDto bookDto = modelMapper.map(element, BookDto.class);
                            bookDto.setCurrentQuantity(element.getBookInventory().getCurrentQuantity());
                            return bookDto;
                        })
                .toList())
                .build();
    }

    @Override
    public BookDetailDto getBookDetailByISBN(String isbn) {
        Optional<Book> bookOptional = bookRepository.findByIsbn13(isbn);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.get();
            BookInventory inventory = book.getBookInventory();

            BookDetailDto bookDetailDto = modelMapper.map(book, BookDetailDto.class);
            bookDetailDto.setCurrentQuantity(inventory.getCurrentQuantity());
            bookDetailDto.setTotalQuantity(inventory.getTotalQuantity());

            LOGGER.info("Get book detail successfully isbn: {}", isbn);

            return bookDetailDto;
        } else {
            throw new NotFoundException("Not found book with isbn " + isbn);
        }
    }

    @Override
    public void importBookDataFromFile(MultipartFile multipartFile) {
        List<BookImportDto> bookImportDtoList = new ArrayList<>();

        try {
            XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
            XSSFSheet sheet = workbook.getSheet("book_import_data");
            int rowIndex = 0;
            for (Row row : sheet) {
                if (checkIfRowIsEmpty(row)) {
                    break;
                }

                if (rowIndex == 0) {
                    rowIndex++;
                    continue;
                }
                Iterator<Cell> cellIterator = row.iterator();
                int cellIndex = 0;

                BookImportDto bookImportDto = new BookImportDto();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    switch (cellIndex) {
                        case 0 -> bookImportDto.setTitle(cell.getStringCellValue());
                        case 1 -> bookImportDto.setIsbn13(String.valueOf(cell.getNumericCellValue()));
                        case 2 -> bookImportDto.setLanguageCode(cell.getStringCellValue());
                        case 3 -> bookImportDto.setNumPages((int) cell.getNumericCellValue());
                        case 4 -> bookImportDto.setPublicationDate(cell.getLocalDateTimeCellValue().toLocalDate());
                        case 5 -> bookImportDto.setPublisherName(cell.getStringCellValue());
                        case 6 -> {
                            String authors = cell.getStringCellValue();
                            String[] authorArray = authors.split(", ");
                            bookImportDto.setAuthors(List.of(authorArray));
                        }
                        case 7 -> bookImportDto.setStatus(cell.getStringCellValue());
                        case 8 -> bookImportDto.setTotalQuantity((int) cell.getNumericCellValue());
                        default -> {

                        }
                    }
                    cellIndex++;
                }
                bookImportDtoList.add(bookImportDto);
            }

//            this.importBookData(bookImportDtoList);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

//    private void importBookData(List<BookImportDto> bookImportDtoList) {
//        for (BookImportDto bookImportDto : bookImportDtoList) {
//            Book book = modelMapper.map(bookImportDto, Book.class);
//
//            Optional<BookLanguage> bookLanguage = bookLanguageRepository.findByLanguageCode(bookImportDto.getLanguageCode());
//            bookLanguage.ifPresent(book::setLanguage);
//
//            Optional<Publisher> publisher = publisherRepository.findByPublisherName(bookImportDto.getPublisherName());
//            publisher.ifPresent(book::setPublisher);
//
//            List<Author> authors = new ArrayList<>();
//            for (String authorName : bookImportDto.getAuthors()) {
//                Optional<Author> author = authorRepository.findByAuthorName(authorName);
//                author.ifPresent(authors::add);
//            }
//            Set<Author> authorSet = new HashSet<>(authors);
//            book.setAuthors(authorSet);
//
//            bookRepository.save(book);
//        }
//    }

    private boolean checkIfRowIsEmpty(Row row) {
        if (row == null) {
            return true;
        }
        if (row.getLastCellNum() <= 0) {
            return true;
        }
        for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++) {
            Cell cell = row.getCell(cellNum);
            if (cell != null && cell.getCellType() != CellType.BLANK && StringUtils.isNotBlank(cell.toString())) {
                return false;
            }
        }
        return true;
    }
}
