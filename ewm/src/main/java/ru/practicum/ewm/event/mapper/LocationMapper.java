package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.model.Location;

@Component
@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location locationDtoToLocation(LocationDto locationDto);
}
