package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.core.exception.ValidationException;
import ru.practicum.stats.dto.CreateEndpointHitDto;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.mapper.EndpointHitMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    @Transactional
    @Override
    public EndpointHitDto addEndpointHit(CreateEndpointHitDto createEndpointHitDto) {
        log.info("Добавление информации об эндпойнте в БД");
        return endpointHitMapper.endpointHitToEndpointHitDto(statsRepository.save(
                endpointHitMapper.createEndpointHitDtoToEndpointHit(createEndpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Выдача статистики");

        if (end.isBefore(start)) {
            throw new ValidationException("Дата окончания события не может быть раньше начала");
        }

        return statsRepository.getStatistics(start, end, uris, unique);
    }
}
