package school.faang.user_service.controller.event;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilters;
import school.faang.user_service.dto.event.EventWithSubscribersDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.mapper.event.EventMapper;
import school.faang.user_service.service.event.EventServiceImpl;

import java.util.List;

@Tag(name = "Events", description = "Operations related to events")
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {
    private final EventServiceImpl eventServiceImpl;
    private final EventMapper eventMapper;

    @Operation(
            summary = "Create a new event",
            description = "Creates a new event based on the provided event details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Event created successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data")
            }
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(
            @Valid @RequestBody
            @Parameter(description = "Event details to be created") EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event createdEvent = eventServiceImpl.create(event);
        return eventMapper.toDto(createdEvent);
    }

    @Operation(
            summary = "Update an existing event",
            description = "Updates an existing event with the provided details.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    @PatchMapping
    public EventWithSubscribersDto updateEvent(
            @Valid @RequestBody
            @Parameter(description = "Event details to be updated") EventDto eventDto) {
        Event event = eventMapper.toEvent(eventDto);
        Event updatedEvent = eventServiceImpl.updateEvent(event);
        Integer subscribersCount = eventServiceImpl.getSubscribersCount(updatedEvent);
        return eventMapper.toEventWithSubscribersDto(updatedEvent, subscribersCount);
    }

    @Operation(
            summary = "Get event by ID",
            description = "Retrieves an event by its ID.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Event retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    @GetMapping("/{id}")
    public EventDto getEvent(
            @PathVariable("id")
            @Parameter(description = "ID of the event to retrieve") Long eventId) {
        Event event = eventServiceImpl.getEvent(eventId);
        return eventMapper.toDto(event);
    }

    @Operation(
            summary = "Search for events by filter",
            description = "Searches for events based on the provided filter criteria.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of events matching the filter"),
                    @ApiResponse(responseCode = "400", description = "Invalid filter criteria")
            }
    )
    @PostMapping("/search")
    public List<EventDto> getEventsByFilter(
            @RequestBody
            @Parameter(description = "Filter criteria for searching events") EventFilters eventFilters) {
        List<Event> events = eventServiceImpl.getEventsByFilter(eventFilters);
        return eventMapper.toFilteredEventsDto(events);
    }

    @Operation(
            summary = "Get events owned by a user",
            description = "Retrieves events owned by a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of owned events"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/owned-events/{userId}")
    public List<EventDto> getOwnedEvents(
            @PathVariable("userId")
            @Parameter(description = "ID of the user whose owned events are to be retrieved") Long userId) {
        List<Event> events = eventServiceImpl.getOwnedEvents(userId);
        return eventMapper.toDto(events);
    }


    @Operation(
            summary = "Get events participated by a user",
            description = "Retrieves events participated in by a specific user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of participated events"),
                    @ApiResponse(responseCode = "404", description = "User not found")
            }
    )
    @GetMapping("/participated-events/{userId}")
    public List<EventDto> getParticipatedEvents(
            @PathVariable("userId")
            @Parameter(description = "ID of the user whose participated events are to be retrieved") Long userId) {
        List<Event> events = eventServiceImpl.getParticipatedEvents(userId);
        return eventMapper.toDto(events);
    }

    @Operation(
            summary = "Delete an event",
            description = "Deletes an event by its ID.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Event deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Event not found")
            }
    )
    @DeleteMapping("/{id}")
    public void deleteEvent(
            @PathVariable("id")
            @Parameter(description = "ID of the event to delete") Long eventId) {
        eventServiceImpl.deleteEvent(eventId);
    }
}
