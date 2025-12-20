package ru.practicum.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.dto.UpdateEventUserRequest;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class EventPrivateController {

    private final EventService eventService;
    private final RequestService requestService;

    @PostMapping
    public EventFullDto create(@PathVariable Long userId,
                               @RequestBody @Valid NewEventDto request) {
        return eventService.saveEvent(userId, request);
    }

    @GetMapping
    public Collection<EventShortDto> getEventsUser(@PathVariable Long userId,
                                                   @RequestParam(defaultValue = "0") Integer from,
                                                   @RequestParam(defaultValue = "10") Integer size) {
        return eventService.getEventsUser(userId, from, size);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventUser(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventUser(userId, eventId);
    }

    @PatchMapping("/{eventId}")
    public EventFullDto updateEventUser(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody @Valid UpdateEventUserRequest request) {
        return eventService.updateEventUser(userId, eventId, request);
    }

    @GetMapping("/{eventId}/requests")
    public Collection<ParticipationRequestDto> getRequestUser(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.getRequestUser(userId, eventId);
    }

    @PatchMapping("{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return requestService.updateRequestStatus(userId, eventId, request);
    }
}
