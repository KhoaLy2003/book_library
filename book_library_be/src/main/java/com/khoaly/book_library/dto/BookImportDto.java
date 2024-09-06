package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookImportDto {
    private String title;
    private String isbn13;
    private String languageCode;
    private Integer numPages;
    private LocalDate publicationDate;
    private String publisherName;
    private List<String> authors;
    private String status;
    private Integer totalQuantity;
}
