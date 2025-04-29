package ru.practicum.ewm.client;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.partrequest.ParticipationRequestDto;

import java.util.List;
import java.util.Map;

@FeignClient(name = "request-service", path = "/internal/requests")
public interface ParticipationRequestClient {

    @GetMapping("/prepare-confirmed-requests")
    Map<Long, List<ParticipationRequestDto>> prepareConfirmedRequests(@RequestParam List<Long> eventIds)
            throws FeignException;

    @GetMapping("/{eventId}")
    List<ParticipationRequestDto> findAllByEventId(@PathVariable Long eventId) throws FeignException;

    @GetMapping
    List<ParticipationRequestDto> findAllById(@RequestParam List<Long> requestIds) throws FeignException;

    @PutMapping("/{requestId}")
    void updateRequestStatus(@PathVariable Long requestId, @RequestParam String status);
}
