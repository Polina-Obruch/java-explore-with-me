package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.model.Event;

import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event>, EventRepositoryCustom {

    List<Event> findAllByInitiatorId(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event as e " +
            "JOIN Review as r ON e.id = r.event.id " +
            "GROUP BY e.id ")
    List<Event> findEventWithReview();

    //Берем Set для уникальности подборки
    Set<Event> findAllByIdIn(List<Long> eventIds);
}
