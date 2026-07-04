package com.tpe.cinetime.exception;

import com.tpe.cinetime.payload.responseMessage.ResponseMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import javax.validation.ConstraintViolationException;

import java.util.List;


@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    //Handles resource not found errors
    @ExceptionHandler({NotFoundException.class, ResourceNotFoundException.class})
    public ResponseEntity<ResponseMessage<?>> handleNotFoundException(RuntimeException exception){

        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseMessage<?>> handleUnreadableRequest(HttpMessageNotReadableException exception) {
        log.debug("Unreadable request body", exception);
        return error(HttpStatus.BAD_REQUEST, "Request body is invalid or contains an unsupported value");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseMessage<?>> handleConstraintViolation(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations().stream()
                .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                .toList();

        ResponseMessage<List<String>> response = ResponseMessage.<List<String>>builder()
                .object(errors)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .build();
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ResponseMessage<?>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception) {
        log.warn("Data integrity violation: {}", exception.getMostSpecificCause().getMessage());
        return error(HttpStatus.CONFLICT, "Request conflicts with existing data");
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ResponseMessage<?>> handleEmailSendException(EmailSendException exception) {
        log.error("Email delivery failed", exception);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "Email service is temporarily unavailable");
    }

    //Handles invalid request errors
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ResponseMessage<?>> handleBadRequestException(BadRequestException exception){

        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Handles validation errors for @Valid annotated request bodies
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseMessage<?>> handleValidationException(MethodArgumentNotValidException exception){

        List<String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();

        ResponseMessage<List<String>> response = ResponseMessage.<List<String>>builder()
                .object(errors)
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("Validation failed")
                .build();

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    //Handles wrong email or password errors during login
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseMessage<?>> handleBadCredentialsException(
            BadCredentialsException exception
    ){
        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .message("Email or password is incorrect")
                .build();

        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }


    // Handles forbidden access errors for authenticated users without required role
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseMessage<?>> handleAccessDeniedException(AccessDeniedException exception){

        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .message("Access is denied")
                .build();

        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }
    //Handles all other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<?>> handleAllUnknownExceptions(Exception exception){
        log.error("Unexpected server error", exception);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
    }

    private ResponseEntity<ResponseMessage<?>> error(HttpStatus status, String message) {
        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(status)
                .message(message)
                .build();
        return ResponseEntity.status(status).body(response);
    }
}

