package ru.practicum.ewm.core.utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.ewm.core.exception.ValidationException;

public class PaginationUtils extends PageRequest {

    protected PaginationUtils(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public static Pageable toMakePage(Integer from, Integer size) {
        if (from == null || size == null) {
            return null;
        }

        if (size <= 0 || from < 0) {
            throw new ValidationException("Уточнчите правильность параметров отображения");
        }

        int page = from / size;
        return PageRequest.of(page, size);
    }
}
