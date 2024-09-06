package com.khoaly.book_library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class BaseExceptionResponse {
    private int status;
    private String message;
    private Object content;
}
