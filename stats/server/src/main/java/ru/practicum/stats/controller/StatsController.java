package ru.practicum.stats.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;
    private final EndpointHitMapper endpointHitMapper;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto saveEndpointHit(@Valid @RequestBody CreateEndpointHitDto createEndpointHitDto) {
        log.info("Запрос на сохранение информации обращения к эндпойнту");
        return endpointHitMapper.endpointHitToEndpointHitDto(
                statsService.addEndpointHit(
                        endpointHitMapper.createEndpointHitDtoToEndpointHit(createEndpointHitDto)));
    }

    @GetMapping("/stats")
    public List<ViewStats> getStatistics(@RequestParam LocalDateTime start,
                                         @RequestParam LocalDateTime end,
                                         @RequestParam(required = false) List<String> uris,
                                         @RequestParam(defaultValue = "false") boolean unique) {
        log.info("Запрос на выдачу статистики по списку uris");
        return statsService.getStatistics(start, end, uris, unique);
    }

}
