package com.team1206.pos.exceptions;

import io.micrometer.common.lang.NonNull;
import org.springframework.beans.factory.annotation.Value;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Value("${spring.profiles.active:prod}")
    private String activeProfile;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.NOT_FOUND.value());
        errorObject.setIdentifier(ex.getUuid());
        errorObject.setMessage(String.format("%s not found", ex.getResourceType().getDisplayName()));
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateExceptionWithId.class)
    public ResponseEntity<ErrorObject> handleIllegalStateException(IllegalStateExceptionWithId ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.CONFLICT.value()); // Uses 409 Conflict
        errorObject.setIdentifier(ex.getUuid());
        errorObject.setMessage(ex.getMessage());
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        // Collect detailed validation error messages
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.BAD_REQUEST.value());
        errorObject.setMessage("Validation failed");
        errorObject.setDetails(errors); // Attach field-specific error details
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SnsServiceException.class)
    public ResponseEntity<ErrorObject> handleSmsServiceException(SnsServiceException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.SERVICE_UNAVAILABLE.value()); // Use 503 Service Unavailable
        errorObject.setMessage(ex.getMessage());
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        // Add additional details in dev mode
        if ("dev".equalsIgnoreCase(activeProfile)) {
            Map<String, String> details = new HashMap<>();
            details.put("cause", ex.getCause() != null ? ex.getCause().getMessage() : "Unknown cause");
            // Add custom details if the exception contains AWS-specific metadata
            if (ex.getCause() instanceof software.amazon.awssdk.services.sns.model.SnsException snsException) {
                details.put("AWS Service Name", snsException.awsErrorDetails().serviceName());
                details.put("AWS Error Code", snsException.awsErrorDetails().errorCode());
            }
            errorObject.setDetails(details);
        }

        return new ResponseEntity<>(errorObject, HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorObject> handleUnauthorizedActionException(UnauthorizedActionException ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.FORBIDDEN.value()); // 403 Forbidden
        errorObject.setMessage(ex.getMessage());
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    // Handle generic exceptions (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGenericException(Exception ex, WebRequest request) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setIdentifier("");
        errorObject.setMessage(ex.getMessage());
        errorObject.setPath(request.getDescription(false).replace("uri=", ""));
        errorObject.setTimestamp(LocalDateTime.now());

        // Add stack trace in development mode
        if ("dev".equalsIgnoreCase(activeProfile)) {
            errorObject.setDetails(Collections.singletonMap("stackTrace", getStackTrace(ex)));
        }

        return new ResponseEntity<>(errorObject, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to get the stack trace as a string
    private String getStackTrace(Exception ex) {
        StringBuilder stackTrace = new StringBuilder();
        for (StackTraceElement element : ex.getStackTrace()) {
            stackTrace.append(element.toString()).append("\n");
        }
        return stackTrace.toString();
    }
}
