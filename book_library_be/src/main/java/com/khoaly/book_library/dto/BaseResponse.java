package com.khoaly.book_library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BaseResponse {
    private int status;
    private String message;
    private Object data;
}
