package com.khoaly.book_library.dto;

import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
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
public class BorrowBookHistoryDto implements Serializable {
    private Integer id;
    private String memberMembershipNumber;
    private BorrowingStatusEnum status;
    private LocalDate borrowingDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private Integer totalBook;
}
