package com.example.microserviceboilerplate.controller.advice;

import com.example.microserviceboilerplate.exception.SomeCustomException;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Log4j2
public class GlobalExceptionHandler {

    @Autowired
    private HttpHeaders httpHeaders;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public String handleGenericException(Exception ex) {
        log.error(ex.getMessage(), ex);
        return "some error response";
    }

    @ExceptionHandler(SomeCustomException.class)
    public ResponseEntity handleCustomException(SomeCustomException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .headers(httpHeaders)
                .body(null);
    }
}
