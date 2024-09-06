package com.khoaly.book_library.exception;

public class MaximumBorrowLimitException extends RuntimeException {
    public MaximumBorrowLimitException(String message) {
        super(message);
    }
}
