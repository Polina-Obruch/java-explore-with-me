package ru.practicum.ewm.core.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.core.exception.ConflictException;
import ru.practicum.ewm.core.exception.ValidationException;
import ru.practicum.ewm.core.exception.model.ApiError;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBindException(MethodArgumentNotValidException exp) {
        //Ошибок валидации может быть несколько - возвращаем информацию по всем полям
        Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField,
                        Objects.requireNonNull(DefaultMessageSourceResolvable::getDefaultMessage)));
        log.error(errors.toString());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                errors.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exp) {
        log.error(exp.getMessage());
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                exp.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleRequiredRequestParameter(final MissingServletRequestParameterException exp) {
        log.error(exp.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exp.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final RuntimeException exp) {
        log.error(exp.getMessage());
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exp.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidation(final ConflictException exp) {
        log.error(exp.getMessage());
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                exp.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityException(DataAccessException exception) {
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exception.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable exp) {
        log.error("Произошла непредвиденная ошибка.{}", exp.getMessage());
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exp.getClass().getName(),
                exp.getMessage(),
                LocalDateTime.now());
    }
}
