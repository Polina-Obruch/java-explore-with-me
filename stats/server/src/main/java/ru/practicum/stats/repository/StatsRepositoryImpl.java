package ru.practicum.stats.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.stats.dto.ViewStats;
import ru.practicum.stats.mapper.ViewStatsMapper;
import ru.practicum.stats.model.QEndpointHit;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StatsRepositoryImpl implements StatsRepositoryCustom {
    private final EntityManager entityManager;
    private final ViewStatsMapper viewStatsMapper;

    //Используем Querydsl для удобства, так как параметров запроса статистики много
    // и они являются динамичными/необязательными
    @Override
    public List<ViewStats> getStatistics(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        QEndpointHit qEndpointHit = QEndpointHit.endpointHit;
        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);

        //Условие поиска по БД
        BooleanExpression expression = qEndpointHit.timestamp.between(start, end);

        //Если необходимо выдать конкретные uri добавляем новое условие поиска
        if (uris != null && !uris.isEmpty()) {
            expression = expression.and(qEndpointHit.uri.in(uris));
        }

        //Создание псевдонима количества посещений, для возможности ссылаться в orderBy
        NumberPath<Long> count = Expressions.numberPath(Long.class, "c");

        List<Tuple> result = queryFactory.select(
                        qEndpointHit.app, qEndpointHit.uri,
                        unique ? qEndpointHit.ip.countDistinct().as(count) : qEndpointHit.ip.count().as(count))
                .from(qEndpointHit)
                .where(expression)
                .groupBy(qEndpointHit.app, qEndpointHit.uri)
                .orderBy(count.desc())
                .fetch();


        return result.stream().map(viewStatsMapper::tupleToViewStats).collect(Collectors.toList());
    }
}
