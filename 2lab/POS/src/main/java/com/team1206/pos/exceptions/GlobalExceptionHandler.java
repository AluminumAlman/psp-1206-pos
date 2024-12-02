package com.team1206.pos.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MerchantNotFoundException.class)
    public ResponseEntity<ErrorObject> handleMerchantNotFoundException(MerchantNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorObject.setUuid(ex.getMessage());
        errorObject.setMessage("Merchant not found");
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    // Handle generic exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setUuid(ex.getMessage());
        errorObject.setMessage("Internal Server Error");
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
