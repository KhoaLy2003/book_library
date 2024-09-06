package com.khoaly.book_library.exception;

public class InvalidBookToBorrowException extends RuntimeException{
    public InvalidBookToBorrowException(String message) {
        super(message);
    }
}
