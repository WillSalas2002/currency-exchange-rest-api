package com.will.currency.exchange.api.exception;

public class CustomException extends Throwable {
    public CustomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}