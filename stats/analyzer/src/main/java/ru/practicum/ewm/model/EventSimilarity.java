package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "events_similarity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_a")
    private Long eventA;

    @Column(name = "event_b")
    private Long eventB;

    private Float score;

    private Instant timestamp;
}
