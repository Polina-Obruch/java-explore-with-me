package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RequestStatusUpdateDto {
    private List<Long> requestIds;
    private RequestStatus status;
}
