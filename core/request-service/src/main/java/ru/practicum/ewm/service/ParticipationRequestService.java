package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.partrequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.partrequest.enums.Status;

import java.util.List;
import java.util.Map;

public interface ParticipationRequestService {

    ParticipationRequestDto addRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<ParticipationRequestDto> getAllUserRequests(Long userId);

    Map<Long, List<ParticipationRequestDto>> prepareConfirmedRequests(List<Long> eventIds);

    List<ParticipationRequestDto> findAllByEventId(Long eventId);

    List<ParticipationRequestDto> findAllById(List<Long> requestIds);

    void updateRequestStatus(Long requestId, Status status);
}
