package ru.practicum.ewm.core.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.core.exception.EntityNotFoundException;
import ru.practicum.ewm.core.exception.model.ErrorResponse;
import ru.practicum.ewm.core.exception.model.ValidationErrorResponse;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ValidationErrorResponse handleBindException(MethodArgumentNotValidException exp) {
        //Ошибок валидации может быть несколько - возвращаем информацию по всем полям
        Map<String, String> errors = exp.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField,
                        Objects.requireNonNull(DefaultMessageSourceResolvable::getDefaultMessage)));
        log.error(errors.toString());
        return new ValidationErrorResponse(errors);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException exp) {
        log.error(exp.getMessage());
        return new ErrorResponse(
                HttpStatus.NOT_FOUND,
                "The required object was not found.",
                exp.getMessage(),
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable exp) {
        log.error("Произошла непредвиденная ошибка.{}", exp.getMessage(), exp);
        return new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                exp.getClass().getName(),
                exp.getMessage(),
                LocalDateTime.now());
    }
}
