package com.efuture.product.exception;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InvalidRequestException extends RuntimeException {
    private String description;
    public InvalidRequestException(String message,String description) {
        super(message);
        this.description = description;
    }
}
