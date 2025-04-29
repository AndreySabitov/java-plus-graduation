package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.client.EventClient;
import ru.practicum.ewm.client.UserClient;
import ru.practicum.ewm.dto.event.EventFullDto;
import ru.practicum.ewm.dto.event.enums.State;
import ru.practicum.ewm.dto.partrequest.ParticipationRequestDto;
import ru.practicum.ewm.dto.partrequest.enums.Status;
import ru.practicum.ewm.exception.ConflictDataException;
import ru.practicum.ewm.exception.DuplicateException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.repository.ParticipationRequestRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParticipationRequestServiceImpl implements ParticipationRequestService {
    private final ParticipationRequestRepository requestRepository;
    private final UserClient userClient;
    private final EventClient eventClient;

    @Transactional
    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {
        if (eventId == 0) {
            throw new ValidationException("Не задано id события");
        }

        if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь c id: " + userId + " не найден");
        }

        EventFullDto event = eventClient.findEventById(eventId);

        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new DuplicateException("Такой запрос уже существует");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictDataException("Пользователь не может создать запрос на участие в своем же событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictDataException("Нельзя участвовать в неопубликованном событии");
        }
        Integer participantLimit = event.getParticipantLimit();
        Integer confirmedRequests = event.getConfirmedRequests();
        if (!participantLimit.equals(0) && participantLimit.equals(confirmedRequests)) {
            throw new ConflictDataException("Лимит запросов на участие в событии уже достигнут");
        }
        Status status;
        if (participantLimit.equals(0) || !event.getRequestModeration()) {
            status = Status.CONFIRMED;
            eventClient.updateConfirmedRequests(eventId, ++confirmedRequests);
        } else status = Status.PENDING;

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requesterId(userId)
                .eventId(event.getId())
                .status(status)
                .build();
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.save(participationRequest));
    }

    @Transactional
    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        checkExistsUserById(userId);
        ParticipationRequest request = requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException("У пользователя с id: " + userId +
                        " не найдено запроса с id: " + requestId));
        if (request.getStatus() == Status.CONFIRMED) {
            EventFullDto event = eventClient.findEventById(request.getEventId());
            Integer confirmedRequests = event.getConfirmedRequests();
            eventClient.updateConfirmedRequests(event.getId(), --confirmedRequests);
        }
        request.setStatus(Status.CANCELED);
        return ParticipationRequestMapper.toParticipationRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getAllUserRequests(Long userId) {
        checkExistsUserById(userId);
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public Map<Long, List<ParticipationRequestDto>> prepareConfirmedRequests(List<Long> eventIds) {
        log.info("Получаем список подтверждённых запросов для всех событий.");

        List<ParticipationRequestDto> confirmedRequests = ParticipationRequestMapper
                .toParticipationRequestDto(requestRepository.findConfirmedRequests(eventIds));

        Map<Long, List<ParticipationRequestDto>> result = new HashMap<>();

        for (ParticipationRequestDto request : confirmedRequests) {
            var eventId = request.getEvent();
            List<ParticipationRequestDto> list = result.get(eventId);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(request);
            result.put(eventId, list);
        }
        return result;
    }

    @Override
    public List<ParticipationRequestDto> findAllByEventId(Long eventId) {
        return ParticipationRequestMapper
                .toParticipationRequestDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    public List<ParticipationRequestDto> findAllById(List<Long> requestIds) {
        return ParticipationRequestMapper.toParticipationRequestDto(requestRepository.findAllById(requestIds));
    }

    @Transactional
    @Override
    public void updateRequestStatus(Long requestId, Status status) {
        ParticipationRequest request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Запрос на участие не найден"));
        request.setStatus(status);
    }

    private void checkExistsUserById(Long userId) {
        if (!userClient.checkExistsById(userId)) {
            throw new NotFoundException("Пользователь c id: " + userId + " не найден");
        }
    }
}
