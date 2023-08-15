package ru.practicum.ewm.event.model;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@Builder
@Table(name = "LOCATION")
@AllArgsConstructor
@NoArgsConstructor
public class Location {
    @Id
    @Column(name = "LOCATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "LATITUDE")
    private BigDecimal lat;

    @Column(name = "LONGITUDE")
    private BigDecimal lon;
}
