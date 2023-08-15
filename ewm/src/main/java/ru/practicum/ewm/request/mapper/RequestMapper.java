package ru.practicum.ewm.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.request.dto.AnswerStatusUpdateDto;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.AnswerStatusUpdate;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    RequestDto requestToRequestDto(Request request);

    default List<RequestDto> requestListToRequestDtoList(List<Request> requests) {
        return requests.stream().map(this::requestToRequestDto).collect(Collectors.toList());
    }

    default AnswerStatusUpdateDto answerStatusUpdateToAnswerStatusUpdateDto(AnswerStatusUpdate answerStatusUpdate) {
        List<RequestDto> confirmedRequest = answerStatusUpdate.getConfirmedRequests().stream()
                .map(this::requestToRequestDto).collect(Collectors.toList());

        List<RequestDto> rejectedRequest = answerStatusUpdate.getRejectedRequests().stream()
                .map(this::requestToRequestDto).collect(Collectors.toList());

        return new AnswerStatusUpdateDto(confirmedRequest, rejectedRequest);
    }
}
