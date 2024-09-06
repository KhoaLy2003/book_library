package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

/**
 * DTO for {@link com.khoaly.book_library.entity.Borrowing}
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BorrowingDto implements Serializable {
    @NotNull(message = "Account borrow id is required")
    private int accountBorrowId;

    @NotNull(message = "The borrowing date is required")
    private LocalDate borrowingDate;

    @NotNull(message = "The due date is required")
    @Future(message = "Due date must at future")
    private LocalDate dueDate;

    @Valid
    @NotNull
    private List<BorrowingDetailDto> bookList;
}