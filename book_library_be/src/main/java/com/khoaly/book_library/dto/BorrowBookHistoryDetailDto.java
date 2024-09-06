package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowBookHistoryDetailDto implements Serializable {
    private Integer id;
    private Integer bookId;
    private String bookTitle;
    private String bookIsbn13;
    private Integer borrowAmount;
    private Integer returnAmount;
}
