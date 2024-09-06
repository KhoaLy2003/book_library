package com.khoaly.book_library.service;

import com.khoaly.book_library.dto.BookDetailDto;
import com.khoaly.book_library.dto.BookDto;
import com.khoaly.book_library.dto.PageResponse;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {
    PageResponse<BookDto> getBooks(int pageNo, String title);
    BookDetailDto getBookDetailByISBN(String isbn);
    void importBookDataFromFile(MultipartFile multipartFile);
}
