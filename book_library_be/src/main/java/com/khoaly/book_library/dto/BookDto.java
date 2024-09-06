package com.khoaly.book_library.dto;

import com.khoaly.book_library.enumeration.BookStatusEnum;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO for {@link com.khoaly.book_library.entity.Book}
 */
@NoArgsConstructor
@Getter
@Setter
public class BookDto implements Serializable {
    private Integer id;
    private String title;
    private String isbn13;
    private Integer numPages;
    private LocalDate publicationDate;
    private BookStatusEnum status;
    private Integer currentQuantity;
}