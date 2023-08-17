package ru.practicum.ewm.user.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findAllByIdIn(List<Long> userIds, Pageable pageable);

    @Query("SELECT u FROM User as u " +
            "JOIN Event as e ON u.id = e.initiator.id " +
            "GROUP BY u.id")
    List<User> findAllInitiatorEvent();
}
