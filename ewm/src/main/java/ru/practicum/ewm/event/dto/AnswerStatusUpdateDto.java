package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerStatusUpdateDto {
    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
