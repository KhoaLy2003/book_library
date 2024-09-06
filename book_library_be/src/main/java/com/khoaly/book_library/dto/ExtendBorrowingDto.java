package com.khoaly.book_library.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExtendBorrowingDto implements Serializable {
    @NotNull(message = "Borrowing id is required")
    private int borrowingId;

    @NotNull(message = "The new due date is required")
    @Future(message = "The new due date must be in the future")
    private LocalDate newDueDate;
}
