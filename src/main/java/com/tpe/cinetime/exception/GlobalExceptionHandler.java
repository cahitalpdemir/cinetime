package com.tpe.cinetime.exception;

import com.tpe.cinetime.payload.response.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;


@ControllerAdvice
public class GlobalExceptionHandler {

    //Handles resource not found errors
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ResponseMessage<?>> handleNotFoundException(NotFoundException exception){

        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
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

    //Handles all other unexpected exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseMessage<?>> handleAllUnknownExceptions(Exception exception){

        ResponseMessage<?> response = ResponseMessage.builder()
                .httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }



}
