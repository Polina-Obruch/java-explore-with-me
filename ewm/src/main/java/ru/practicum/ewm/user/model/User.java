package ru.practicum.ewm.user.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "USERS")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;
}
