package ru.practicum.ewm.request.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Этот класс можно не называть через Dto, так как никакого маппинга не происходит с ним
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdate {
    private List<Long> requestIds;
    private RequestStatus status;
}
