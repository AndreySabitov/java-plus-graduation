package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "user_actions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private Long eventId;

    @Column(name = "user_id")
    private Long userId;

    private Float mark;

    private Instant timestamp;
}
