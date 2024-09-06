package com.khoaly.book_library.exception;

public class InvalidBorrowPeriodExtensionException extends RuntimeException{
    public InvalidBorrowPeriodExtensionException(String message) {
        super(message);
    }
}
