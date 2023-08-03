package ru.practicum.ewm.core.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus status;
    private String reason;
    private String message;
    private LocalDateTime timestamp;
}
