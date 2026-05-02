package com.assignment3.project.services;

import com.assignment3.project.dto.requests.VolunteerEventCreateRequest;
import com.assignment3.project.dto.requests.VolunteerEventUpdateRequest;
import com.assignment3.project.dto.responses.VolunteerEventResponse;
import com.assignment3.project.entities.User;
import com.assignment3.project.entities.VolunteerEvent;
import com.assignment3.project.enums.UserRole;
import com.assignment3.project.mappers.VolunteerEventMapper;
import com.assignment3.project.repositories.VolunteerEventRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VolunteerEventService {
    private final VolunteerEventRepository volunteerEventRepository;
    private final VolunteerEventMapper volunteerEventMapper;
    private final UserService userService;

    public List<VolunteerEventResponse> getAllEvents() {
        log.info("VolunteerEventService.getAllEvents called");
        List<VolunteerEvent> events = volunteerEventRepository.findAll();
        return events.stream().map(volunteerEventMapper::toDto).toList();
    }

    public VolunteerEventResponse getEventById(Long eventId) {
        log.info("VolunteerEventService.getEventById called id={}", eventId);
        VolunteerEvent event = volunteerEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        return volunteerEventMapper.toDto(event);
    }

    @Transactional
    public VolunteerEventResponse createEvent(VolunteerEventCreateRequest request) {
        log.info("VolunteerEventService.createEvent called organizerId={} title={}", request.getOrganizerId(), request.getTitle());
        User organizer = userService.getUserByIdOrThrow(request.getOrganizerId());

        if (organizer.getRole() != UserRole.ADMIN) {
            throw new AccessDeniedException("Only administrators can create events");
        }

        VolunteerEvent event = new VolunteerEvent();
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getEventDate());
        event.setLocation(request.getLocation());
        event.setOrganizer(organizer);

        VolunteerEvent saved = volunteerEventRepository.save(event);
        return volunteerEventMapper.toDto(saved);
    }

    @Transactional
    public VolunteerEventResponse updateEvent(Long eventId, VolunteerEventUpdateRequest request) {
        log.info("VolunteerEventService.updateEvent called id={} title={}", eventId, request.getTitle());
        VolunteerEvent event = volunteerEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User currentUser = userService.getCurrentUser();
        Long organizerId = event.getOrganizer().getId();
        Long currentUserId = currentUser.getId();
        boolean isOrganizer = organizerId.equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("Only event organizer or administrator can update the event");
        }

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        if (request.getEventDate() != null) {
            event.setEventDate(request.getEventDate());
        }
        event.setLocation(request.getLocation());

        VolunteerEvent saved = volunteerEventRepository.save(event);
        return volunteerEventMapper.toDto(saved);
    }

    @Transactional
    public void deleteEvent(Long eventId) {
        log.info("VolunteerEventService.deleteEvent called id={}", eventId);
        VolunteerEvent event = volunteerEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User currentUser = userService.getCurrentUser();
        Long organizerId = event.getOrganizer().getId();
        Long currentUserId = currentUser.getId();
        boolean isOrganizer = organizerId.equals(currentUserId);
        boolean isAdmin = currentUser.getRole() == UserRole.ADMIN;

        if (!isOrganizer && !isAdmin) {
            throw new AccessDeniedException("Only event organizer or administrator can delete the event");
        }

        volunteerEventRepository.deleteById(eventId);
        log.info("Event with id {} deleted", eventId);
    }

    @Transactional
    public VolunteerEventResponse joinEvent(Long eventId) {
        log.info("VolunteerEventService.joinEvent called eventId={}", eventId);
        VolunteerEvent event = volunteerEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User currentUser = userService.getCurrentUser();

        if (event.getParticipants().contains(currentUser)) {
            throw new IllegalArgumentException("User is already registered for this event");
        }

        event.getParticipants().add(currentUser);
        VolunteerEvent saved = volunteerEventRepository.save(event);
        return volunteerEventMapper.toDto(saved);
    }

    @Transactional
    public VolunteerEventResponse leaveEvent(Long eventId) {
        log.info("VolunteerEventService.leaveEvent called eventId={}", eventId);
        VolunteerEvent event = volunteerEventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        User currentUser = userService.getCurrentUser();

        if (!event.getParticipants().contains(currentUser)) {
            throw new IllegalArgumentException("User is not registered for this event");
        }

        event.getParticipants().remove(currentUser);
        VolunteerEvent saved = volunteerEventRepository.save(event);
        return volunteerEventMapper.toDto(saved);
    }
}

