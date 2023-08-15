package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<Request> findAllByRequesterId(Long userId);

    //Выдача заявок для создателя события
    List<Request> findAllByEventIdAndEventInitiatorId(Long eventId, Long userId);

    @Query("SELECT count(req) FROM Request req " +
            "WHERE req.event.id = :eventId AND req.status = 'CONFIRMED'")
    Integer findCountOfEventConfirmedRequests(@Param("eventId") Long eventId);

    @Query("SELECT req FROM Request req " +
            "WHERE req.event.id IN :eventId AND req.status = 'CONFIRMED'")
    List<Request> findAllConfirmedRequestsByEventIdIn(List<Long> eventId);

    @Query("SELECT req FROM Request req " +
            "WHERE req.id in :requestIds AND req.event.id = :eventId AND req.event.initiator.id = :initiatorId " +
            "ORDER BY req.created ASC")
    List<Request> getRequestsForUpdating(@Param("eventId") Long eventId, @Param("initiatorId") Long initiatorId,
                                         @Param("requestIds") List<Long> requestIds);
}
