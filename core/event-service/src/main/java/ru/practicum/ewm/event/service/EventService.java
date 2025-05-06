package ru.practicum.ewm.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.event.*;
import ru.practicum.ewm.dto.partrequest.ParticipationRequestDto;

import java.util.List;

public interface EventService {
    EventFullDto addEvent(NewEventDto eventDto, Long userId);

    List<EventShortDto> getEventsOfUser(Long userId, Integer from, Integer size);

    EventFullDto getEventOfUser(Long userId, Long eventId);

    EventFullDto updateEventOfUser(UpdateEventUserRequest updateRequest, Long userId, Long eventId);

    List<EventShortDto> getPublicEventsByFilter(HttpServletRequest httpServletRequest, EventPublicFilter inputFilter);

    EventFullDto getPublicEventById(long userId, Long id);

    List<EventFullDto> getEventsForAdmin(EventAdminFilter admin);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventAdminRequest updateEventAdminRequest);

    List<ParticipationRequestDto> getRequestsOfUserEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest, Long userId,
                                                        Long eventId);

    EventFullDto findEventById(Long eventId);

    void updateConfirmedRequests(Long eventId, Integer confirmedRequests);

    boolean checkExistsById(Long eventId);

    void deleteEventsByUser(Long userId);

    List<EventShortDto> getEventsRecommendations(Long userId, int maxResults);

    void addLikeToEvent(Long eventId, Long userId);
}
