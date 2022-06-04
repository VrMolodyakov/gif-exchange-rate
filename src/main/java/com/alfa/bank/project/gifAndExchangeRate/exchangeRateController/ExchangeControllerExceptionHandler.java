package com.alfa.bank.project.gifAndExchangeRate.exchangeRateController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class ExchangeControllerExceptionHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(ExchangeControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(Exception.class)
    public String notFoundHandler() {
        LOGGER.warn("Currency not found. HTTP 500 returned.");
        return "error";
    }
}
