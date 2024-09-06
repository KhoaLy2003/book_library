package com.khoaly.book_library.exception;

import com.khoaly.book_library.constant.MessageConstant;
import com.khoaly.book_library.enumeration.BorrowingStatusEnum;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class BaseExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<BaseExceptionResponse> handleNotFoundException(NotFoundException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.NOT_FOUND.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<BaseExceptionResponse> handleDuplicateEmailException(DuplicateEmailException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.CONFLICT.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(InvalidAccountBorrowingException.class)
    public ResponseEntity<BaseExceptionResponse> handleInvalidAccountBorrowingException(InvalidAccountBorrowingException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBookToBorrowException.class)
    public ResponseEntity<BaseExceptionResponse> handleHasInvalidBookToBorrowException(InvalidBookToBorrowException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaximumBorrowLimitException.class)
    public ResponseEntity<BaseExceptionResponse> handleMaximumBorrowLimitException(MaximumBorrowLimitException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaximumBorrowPeriodException.class)
    public ResponseEntity<BaseExceptionResponse> handleMaximumBorrowPeriodException(MaximumBorrowPeriodException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBorrowPeriodExtensionException.class)
    public ResponseEntity<BaseExceptionResponse> handleInvalidBorrowPeriodExtensionException(InvalidBorrowPeriodExtensionException exception, WebRequest request) {
        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), exception.getMessage(),
                request.getDescription(false));
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public ResponseEntity<BaseExceptionResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Accepted values are: %s",
                exception.getValue(), exception.getName(), Arrays.toString(BorrowingStatusEnum.values()));

        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), message, exception.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseExceptionResponse> handleInvalidArgument(MethodArgumentNotValidException ex) {
        Map<String, String> errorMap = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));

        BaseExceptionResponse error = new BaseExceptionResponse(HttpStatus.BAD_REQUEST.value(), MessageConstant.INVALID_INPUT_DATA, errorMap);
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
