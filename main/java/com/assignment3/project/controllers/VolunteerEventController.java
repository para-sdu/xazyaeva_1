package com.assignment3.project.controllers;

import com.assignment3.project.dto.requests.VolunteerEventCreateRequest;
import com.assignment3.project.dto.requests.VolunteerEventUpdateRequest;
import com.assignment3.project.dto.responses.VolunteerEventResponse;
import com.assignment3.project.services.VolunteerEventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.version}/events")
public class VolunteerEventController {
    private final VolunteerEventService volunteerEventService;

    @GetMapping()
    public ResponseEntity<List<VolunteerEventResponse>> getAllEvents() {
        List<VolunteerEventResponse> events = volunteerEventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerEventResponse> getEventById(
            @PathVariable("id") Long eventId
    ) {
        VolunteerEventResponse event = volunteerEventService.getEventById(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping()
    public ResponseEntity<VolunteerEventResponse> createEvent(
            @RequestBody @Valid VolunteerEventCreateRequest request
    ) {
        VolunteerEventResponse event = volunteerEventService.createEvent(request);
        return ResponseEntity.ok(event);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VolunteerEventResponse> updateEvent(
            @PathVariable("id") Long eventId,
            @RequestBody @Valid VolunteerEventUpdateRequest request
    ) {
        VolunteerEventResponse event = volunteerEventService.updateEvent(eventId, request);
        return ResponseEntity.ok(event);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable("id") Long eventId
    ) {
        volunteerEventService.deleteEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<VolunteerEventResponse> joinEvent(
            @PathVariable("id") Long eventId
    ) {
        VolunteerEventResponse event = volunteerEventService.joinEvent(eventId);
        return ResponseEntity.ok(event);
    }

    @PostMapping("/{id}/leave")
    public ResponseEntity<VolunteerEventResponse> leaveEvent(
            @PathVariable("id") Long eventId
    ) {
        VolunteerEventResponse event = volunteerEventService.leaveEvent(eventId);
        return ResponseEntity.ok(event);
    }
}

