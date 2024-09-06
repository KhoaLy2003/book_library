package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingDetailDto implements Serializable {
    @NotNull(message = "The book id is required")
    private int bookId;

    @NotNull(message = "The amount to borrow is required")
    @Min(value = 1, message = "Book quantity must equal or greater than 1")
    private int borrowAmount;
}
