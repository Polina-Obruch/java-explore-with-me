package ru.practicum.ewm.core.exception.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ApiErrorForValidation extends ApiError {
    private Map<String, String> errors;

    public ApiErrorForValidation(HttpStatus status, String reason, String message,
                                 Map<String, String> errors, LocalDateTime timestamp) {
        super(status, reason, message, timestamp);
        this.errors = errors;
    }
}
