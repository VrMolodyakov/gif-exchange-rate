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

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public String notFoundHandler(Exception ex) {
        LOGGER.warn("HTTP 500 returned. {} ",ex);
        return "error";
    }
}
