package ru.practicum.stats.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;


@Table(name = "hit")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHit {
    @Id
    @Column(name = "hit_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //Автоматическая генерация базы данных, используем nullable для ограничения к конкретному столбцу
    @Column(nullable = false, length = 64)
    private String app;

    @Column(nullable = false)
    private String uri;

    @Column(nullable = false, length = 15)
    private String ip;

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
