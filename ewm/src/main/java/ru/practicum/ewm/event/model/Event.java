package ru.practicum.ewm.event.model;

import lombok.*;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "EVENTS")
@AllArgsConstructor
@NoArgsConstructor
public class Event {
    @Id
    @Column(name = "EVENT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String annotation;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    private String description;

    @Column(name = "EVENT_DATE")
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "INITIATOR_ID")
    private User initiator;

    @OneToOne
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    private boolean paid;

    @JoinColumn(name = "PARTICIPANT_LIMIT")
    private Integer participantLimit;

    @JoinColumn(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;

    @JoinColumn(name = "REQUEST_MODERATION")
    private boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    private String title;

    @Transient
    private Long views;

    @Transient
    private Long confirmedRequests;
}
