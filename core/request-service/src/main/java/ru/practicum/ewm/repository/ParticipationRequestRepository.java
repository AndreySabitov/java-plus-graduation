package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.dto.partrequest.enums.Status;
import ru.practicum.ewm.model.ParticipationRequest;

import java.util.List;
import java.util.Optional;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    Boolean existsByRequesterIdAndEventId(Long requesterId, Long eventId);

    Optional<ParticipationRequest> findByRequesterIdAndId(Long requesterId, Long requestId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    @Query("select p from ParticipationRequest p where p.status = 'CONFIRMED' and p.eventId in ?1")
    List<ParticipationRequest> findConfirmedRequests(List<Long> ids);

    void deleteByRequesterId(Long requesterId);

    boolean existsByEventIdAndRequesterIdAndStatus(Long eventId, Long userId, Status status);
}
