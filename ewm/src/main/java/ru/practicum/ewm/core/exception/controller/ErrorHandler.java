package ru.practicum.ewm.core.exception.controller;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
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

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBindException(MethodArgumentNotValidException exc) {
        //Ошибок валидации может быть несколько - возвращаем информацию по всем полям
        Map<String, String> errors = exc.getBindingResult().getFieldErrors().stream().collect(
                Collectors.toMap(FieldError::getField,
                        Objects.requireNonNull(DefaultMessageSourceResolvable::getDefaultMessage)));
        log.error(errors.toString(), exc);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                errors.toString(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleEntityNotFoundException(final EntityNotFoundException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleRequiredRequestParameter(final MissingServletRequestParameterException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handlePaginationParameter(final ConstraintViolationException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler(value = {ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(final RuntimeException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.BAD_REQUEST,
                "Incorrectly made request.",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidation(final ConflictException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.CONFLICT,
                "For the requested operation the conditions are not met.",
                exc.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleIntegrityException(DataIntegrityViolationException exc) {
        log.error(exc.getMessage(), exc);
        return new ApiError(
                HttpStatus.CONFLICT,
                "Integrity constraint has been violated.",
                exc.getMessage(),
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleThrowable(final Throwable exc) {
        log.error("Произошла непредвиденная ошибка.{}", exc.getMessage());
        return new ApiError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exc.getClass().getName(),
                exc.getMessage(),
                LocalDateTime.now());
    }
}
