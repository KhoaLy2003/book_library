package com.khoaly.book_library.controller;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.constant.UriConstant;
import com.khoaly.book_library.dto.BaseResponse;
import com.khoaly.book_library.dto.BookDetailDto;
import com.khoaly.book_library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(UriConstant.BOOK_BASE_URI)
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<BaseResponse> getBooks(@RequestParam(name = "pageNo", required = false, defaultValue = "0") int pageNo,
                                                 @RequestParam(name = "title", required = false) String title) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_BOOK_LIST_SUCCESSFULLY, bookService.getBooks(pageNo, title)));
    }

    @GetMapping(UriConstant.BOOK_DETAIL_URI)
//    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<BaseResponse> getBookDetail(@RequestParam(name = "isbn") String isbn) {
        BookDetailDto bookDetailDto = bookService.getBookDetailByISBN(isbn);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_BOOK_DETAIL_SUCCESSFULLY, bookDetailDto));
    }

    @PostMapping("/import")
    public ResponseEntity<BaseResponse> importBookData(@RequestParam("file") MultipartFile file) {
        bookService.importBookDataFromFile(file);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new BaseResponse(HttpStatus.OK.value(), MessageConstant.GET_BOOK_DETAIL_SUCCESSFULLY, file.getName()));
    }
}
