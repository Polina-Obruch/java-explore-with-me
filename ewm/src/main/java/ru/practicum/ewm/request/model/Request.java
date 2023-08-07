package ru.practicum.ewm.request.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "REQUESTS")
public class Request {
    @Id
    @Column(name = "REQUEST_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "REQUESTER_ID")
    private User requester;

    @JoinColumn(name = "CREATED_ON")
    private LocalDateTime created;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;
}
