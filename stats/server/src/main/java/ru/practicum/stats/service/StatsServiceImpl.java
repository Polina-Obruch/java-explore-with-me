package ru.practicum.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.model.EndpointHit;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    @Transactional
    @Override
    public EndpointHit addEndpointHit(EndpointHit endpointHit) {
        log.info("Добавление информации об эндпойнте в БД");
        return statsRepository.save(endpointHit);
    }

    @Override
    public List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        log.info("Выдача статистики");
        return statsRepository.getStatistics(start, end, uris, unique);
    }
}
