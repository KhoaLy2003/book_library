package com.khoaly.book_library.exception;

public class MaximumBorrowPeriodException extends RuntimeException{
    public MaximumBorrowPeriodException(String message) {
        super(message);
    }
}
