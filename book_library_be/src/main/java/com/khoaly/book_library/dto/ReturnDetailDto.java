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
public class ReturnDetailDto implements Serializable {
    @NotNull(message = "The book id is required")
    private int bookId;

    @NotNull(message = "The amount to return is required")
    @Min(value = 0, message = "Book quantity must equal or greater than 0")
    private int returnAmount;
}
