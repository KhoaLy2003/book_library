package com.khoaly.book_library.service;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.dto.BookDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.entity.Book;
import com.khoaly.book_library.entity.BookInventory;
import com.khoaly.book_library.enumeration.BookStatusEnum;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.repository.BookRepository;
import com.khoaly.book_library.service.impl.BookServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource("/test.properties")
class BookServiceTest {
    @Autowired
    private BookServiceImpl bookService;

    @MockBean
    private BookRepository bookRepository;

    private Book book;

    @BeforeEach
    void initData() {
        book = Book.builder()
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
                .currentQuantity(10)
                .totalQuantity(10)
                .build();

        book.setBookInventory(bookInventory);
    }

    @Test
    void getBookDetailByISBN_ShouldReturnSuccess() {
        when(bookRepository.findByIsbn13(anyString())).thenReturn(Optional.of(book));

        var response = bookService.getBookDetailByISBN("9780439554893");

        assertEquals("Harry Potter and the Chamber of Secrets (Harry Potter  #2)", response.getTitle());
    }

    @Test
    void getBookDetailByISBN_ShouldReturnFail() {
        when(bookRepository.findByIsbn13(anyString())).thenReturn(Optional.empty());

        var exception = assertThrows(NotFoundException.class, () -> bookService.getBookDetailByISBN("9780439554893"));

        assertEquals("Not found book with isbn " + "9780439554893", exception.getMessage());
    }

    @Test
    void getBooks_ShouldReturnSuccess() {
        int pageNo = 0;
        int pageSize = CommonConstant.PAGE_SIZE;
        List<Book> books = List.of(book, book);
        Page<Book> pageData = new PageImpl<>(books, PageRequest.of(pageNo, pageSize, Sort.by(CommonConstant.BOOK_SORT_BY).descending()), books.size());

        when(bookRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(pageData);

        PageResponse<BookDto> result = bookService.getBooks(pageNo, "Harry");

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }
}
