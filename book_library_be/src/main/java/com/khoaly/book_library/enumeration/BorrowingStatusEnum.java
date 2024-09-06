package com.khoaly.book_library.enumeration;

import lombok.Getter;

@Getter
public enum BorrowingStatusEnum {
    CREATED,
    CANCELLED,
    BORROWED,
    RETURNED,
    OVERDUE
}
