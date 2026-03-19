package com.pregueapalavra.posGraduationControl.exception.global;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.pregueapalavra.posGraduationControl.exception.exceptions.BusinessException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.DatabaseException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.ResourceNotFoundException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.student.EmailAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.exception.exceptions.subject.NameAlreadyExistsException;
import com.pregueapalavra.posGraduationControl.exception.global.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ValidationErrorResponse handleValidationErrors(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(
                        error.getField(),
                        error.getDefaultMessage()));
        return new ValidationErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation failed",
                errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(ResourceNotFoundException ex) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(DatabaseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDatabaseError(DatabaseException ex) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleEmailAlreadyExists(EmailAlreadyExistsException ex) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(NameAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleNameAlreadyExists(NameAlreadyExistsException ex) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.CONFLICT.value(), ex.getMessage());
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBusiness(BusinessException ex) {
        return new ErrorResponse(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(), ex.getMessage());
    }
}