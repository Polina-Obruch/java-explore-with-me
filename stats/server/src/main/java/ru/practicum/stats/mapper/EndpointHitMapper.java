package ru.practicum.stats.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface EndpointHitMapper {
    //для запросов
    EndpointHit createEndpointHitDtoToEndpointHit(CreateEndpointHitDto createEndpointHitDto);

    //для ответов
    EndpointHitDto endpointHitToEndpointHitDto(EndpointHit endpointHit);
}
