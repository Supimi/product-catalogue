package com.efuture.product.controller;

import com.efuture.product.exception.InvalidRequestException;
import com.efuture.product.util.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


import java.util.ArrayList;
import java.util.List;

import static com.efuture.product.util.Constants.ResponseCodes.STATUS_BAD_REQUEST;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequestException(
            InvalidRequestException ex, WebRequest request) {

        Response<String> response = Response.<String>builder()
                .status(STATUS_BAD_REQUEST)
                .message(ex.getMessage())
                .data(ex.getDescription())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors()
                .forEach((error) -> {
                    String field = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.add(field + ": " + errorMessage);
                });

        Response<String> response = Response.<String>builder()
                .status(STATUS_BAD_REQUEST)
                .message("Validation failed")
                .data(String.join(",", errors))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
