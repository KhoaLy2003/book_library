package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.CommonConstant;
import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BookDetailDto;
import com.khoaly.book_library.dto.BookDto;
import com.khoaly.book_library.dto.PageResponse;
import com.khoaly.book_library.enumeration.BookStatusEnum;
import com.khoaly.book_library.exception.NotFoundException;
import com.khoaly.book_library.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource("/test.properties")
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;

    private PageResponse<BookDto> pageResponse;

    @BeforeEach
    void initData() {
        List<BookDto> bookList = new ArrayList<>();
        int pageSize = CommonConstant.PAGE_SIZE;
        for (int i = 1; i <= pageSize; i++) {

            BookDto bookDto = new BookDto();
            bookDto.setId(i);
            bookDto.setTitle("Harry Potter " + i);
            bookDto.setIsbn13("978-3-16-148410-" + i);
            bookDto.setNumPages(300 + i);
            bookDto.setPublicationDate(LocalDate.now().minusYears(10).plusDays(i));
            bookDto.setStatus(BookStatusEnum.AVAILABLE);
            bookDto.setCurrentQuantity(10 + i);

            bookList.add(bookDto);
        }

        int totalPages = (int) Math.ceil((double) bookList.size() / pageSize);

        pageResponse = PageResponse.<BookDto>builder()
                .currentPage(0)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .totalElements(bookList.size())
                .data(bookList)
                .build();
    }

    @Test
    void getBooks_ShouldReturnSuccess() throws Exception {
        when(bookService.getBooks(anyInt(), anyString())).thenReturn(this.pageResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BOOK_BASE_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("pageNo", "0")
                        .param("title", "Harry Potter"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_BOOK_LIST_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.totalElements").value(10));
    }

    @Test
    void getBookDetail_ShouldReturnSuccess() throws Exception {
        BookDetailDto bookDetailDto = new BookDetailDto();
        bookDetailDto.setId(1);
        bookDetailDto.setTitle("Harry Potter " + 1);
        bookDetailDto.setIsbn13("123456789");
        bookDetailDto.setNumPages(300);
        bookDetailDto.setPublicationDate(LocalDate.now().minusYears(10));
        bookDetailDto.setStatus(BookStatusEnum.AVAILABLE);
        bookDetailDto.setCurrentQuantity(10);
        bookDetailDto.setAuthors("Authors");
        bookDetailDto.setAverageRating(4.9);
        bookDetailDto.setLanguageCode("eng");
        bookDetailDto.setPublisher("Publisher");
        bookDetailDto.setTotalQuantity(10);

        when(bookService.getBookDetailByISBN(anyString())).thenReturn(bookDetailDto);

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BOOK_BASE_URI + UriConstant.BOOK_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("isbn", "123456789"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.OK.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value(MessageConstant.GET_BOOK_DETAIL_SUCCESSFULLY))
                .andExpect(MockMvcResultMatchers.jsonPath("data.id").value(1));
    }

    @Test
    void getBookDetail_ShouldReturnNotFound() throws Exception {
        String isbn = "123456789";
        when(bookService.getBookDetailByISBN(anyString())).thenThrow(new NotFoundException("Not found book with isbn " + isbn));

        mockMvc.perform(MockMvcRequestBuilders.get(UriConstant.BOOK_BASE_URI + UriConstant.BOOK_DETAIL_URI)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("isbn", "123456789"))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(MockMvcResultMatchers.jsonPath("message").value("Not found book with isbn " + isbn));
    }
}
