package ru.practicum.ewm.event.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLTemplates;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.State;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryCustomImpl implements EventRepositoryCustom {
    private final EntityManager entityManager;

    @Override
    public List<Event> findAllByAdminFilters(List<Long> userIds,
                                             List<State> states,
                                             List<Long> categoryIds,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             int from, int size) {
        QEvent qEvent = QEvent.event;
        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        BooleanExpression expression = Expressions.asBoolean(true).isTrue();

        if (rangeStart != null && rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.between(rangeStart, rangeEnd));
        }

        if (userIds != null && !userIds.isEmpty()) {
            expression = expression.and(qEvent.initiator.id.in(userIds));
        }

        if (states != null && !states.isEmpty()) {
            expression = expression.and(qEvent.state.in(states));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            expression = expression.and(qEvent.category.id.in(categoryIds));
        }

        return queryFactory.select(qEvent)
                .from(qEvent)
                .where(expression)
                .offset(from)
                .limit(size)
                .fetch();
    }

    @Override
    public List<Event> findAllByPublicFilters(String text,
                                              List<Long> categoryIds,
                                              Boolean paid,
                                              LocalDateTime rangeStart,
                                              LocalDateTime rangeEnd,
                                              EventSort sort,
                                              int from, int size) {
        QEvent qEvent = QEvent.event;
        JPAQueryFactory queryFactory = new JPAQueryFactory(JPQLTemplates.DEFAULT, entityManager);
        BooleanExpression expression = Expressions.asBoolean(true).isTrue();

        if (text != null && !text.isBlank()) {
            expression = expression.and(qEvent.annotation.containsIgnoreCase(text)
                    .or(qEvent.description.containsIgnoreCase(text)));
        }

        if (categoryIds != null && !categoryIds.isEmpty()) {
            expression = expression.and(qEvent.category.id.in(categoryIds));
        }

        if (rangeStart == null && rangeEnd == null) {
            expression = expression.and(qEvent.eventDate.after(LocalDateTime.now()));
        }

        if (rangeStart != null) {
            expression = expression.and(qEvent.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            expression = expression.and(qEvent.eventDate.before(rangeEnd));
        }

        if (paid != null) {
            expression = expression.and(qEvent.paid.eq(paid));
        }

        OrderSpecifier order = qEvent.id.asc();

        if (sort == EventSort.EVENT_DATE) {
            order = qEvent.eventDate.desc();
        }
        return queryFactory.select(qEvent)
                .from(qEvent)
                .where(expression)
                .offset(from)
                .orderBy(order)
                .limit(size)
                .fetch();
    }
}
