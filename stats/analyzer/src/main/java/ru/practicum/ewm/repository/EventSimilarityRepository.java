package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EventSimilarity;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
}