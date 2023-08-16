package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRatingDto;
import ru.practicum.ewm.event.dto.EventRequestDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "category.id", source = "category")
    Event eventRequestDtoToEvent(EventRequestDto eventRequestDto);

    EventFullDto eventToEventFullDto(Event event);

    List<EventShortDto> listEventsToListEventShortDto(List<Event> events);

    List<EventRatingDto> listEventsToListEventRatingDto(List<Event> events);

    List<EventFullDto> listEventsToListEventFullDto(List<Event> events);

}
