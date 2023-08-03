package ru.practicum.ewm.core.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String nameEntity, Long id) {
        super(nameEntity + " with id=" + id + " not found");
    }
}
