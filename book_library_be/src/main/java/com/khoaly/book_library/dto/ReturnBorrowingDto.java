package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReturnBorrowingDto implements Serializable {
    @NotNull(message = "Borrowing id is required")
    private int borrowingId;

    @Valid
    @NotNull
    private List<ReturnDetailDto> bookList;
}
