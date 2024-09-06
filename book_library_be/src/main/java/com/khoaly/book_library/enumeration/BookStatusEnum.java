package com.khoaly.book_library.enumeration;

import lombok.Getter;

@Getter
public enum BookStatusEnum {
    AVAILABLE,
    BORROWED,
    RESERVED,
    LOST,
    NORMAL,
    DAMAGED,
    CAN_NOT_BORROW,
    OUT_OF_STOCK,
}
