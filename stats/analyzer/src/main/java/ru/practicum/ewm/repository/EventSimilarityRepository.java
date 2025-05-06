package ru.practicum.ewm.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.EventSimilarity;

import java.util.List;
import java.util.Set;

public interface EventSimilarityRepository extends JpaRepository<EventSimilarity, Long> {
    List<EventSimilarity> findAllByEventAIn(Set<Long> eventIds, Sort sort);

    List<EventSimilarity> findAllByEventBIn(Set<Long> eventIds, Sort sort);

    List<EventSimilarity> findAllByEventA(Long eventId, Sort sort);

    List<EventSimilarity> findAllByEventB(Long eventId, Sort sort);

    boolean existsByEventAAndEventB(Long eventA, Long eventB);

    EventSimilarity findByEventAAndEventB(Long eventA, Long eventB);
}