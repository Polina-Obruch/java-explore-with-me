package ru.practicum.stats.mapper;

import com.querydsl.core.Tuple;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    @Mapping(target = "app", expression = "java(tuple.get(0, String.class))")
    @Mapping(target = "uri", expression = "java(tuple.get(1, String.class))")
    @Mapping(target = "hits", expression = "java(tuple.get(2, Long.class))")
    ViewStats tupleToViewStats(Tuple tuple);
}
