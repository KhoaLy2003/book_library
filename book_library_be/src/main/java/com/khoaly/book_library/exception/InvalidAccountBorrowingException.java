package com.khoaly.book_library.exception;

public class InvalidAccountBorrowingException extends RuntimeException{
    public InvalidAccountBorrowingException(String message) {
        super(message);
    }
}
