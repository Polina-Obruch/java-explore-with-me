package ru.practicum.stats.service;

import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    EndpointHit addEndpointHit(EndpointHit endpointHit);

    List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

}
