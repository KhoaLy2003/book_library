package com.khoaly.book_library.dto;

import com.khoaly.book_library.enumeration.BookStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class BookDetailDto implements Serializable {
    private Integer id;
    private String title;
    private String authors;
    private Double averageRating;
    private String isbn;
    private String isbn13;
    private String languageCode;
    private Integer numPages;
    private LocalDate publicationDate;
    private String publisher;
    private BookStatusEnum status;
    private Integer totalQuantity;
    private Integer currentQuantity;
}
