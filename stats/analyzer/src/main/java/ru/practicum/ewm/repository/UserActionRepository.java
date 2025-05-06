package ru.practicum.ewm.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.UserAction;

import java.util.List;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    List<UserAction> findAllByUserId(Long userId, PageRequest pageRequest);

    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    List<UserAction> findAllByEventIdInAndUserId(Set<Long> viewedEvents, Long userId);

    UserAction findByEventIdAndUserId(Long eventId, Long userId);
}
