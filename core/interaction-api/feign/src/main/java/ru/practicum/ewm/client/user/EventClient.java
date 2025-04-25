package ru.practicum.ewm.client.user;

import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.event.EventFullDto;

@FeignClient(name = "main-service", path = "/internal/events")
public interface EventClient {

    @GetMapping("/{eventId}")
    EventFullDto findEventById(@PathVariable Long eventId) throws FeignException;

    @PutMapping("/{eventId}")
    void updateConfirmedRequests(@PathVariable Long eventId, @RequestParam Integer confirmedRequests)
            throws FeignException;
}
