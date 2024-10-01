package school.faang.user_service.controller.event;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.service.event.EventService;
import school.faang.user_service.validator.event.EventValidator;
import school.faang.user_service.validator.groups.CreateGroup;
import school.faang.user_service.validator.groups.UpdateGroup;

import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;
    private final EventValidator eventValidator;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto create(@NotNull @Validated(CreateGroup.class) @RequestBody EventDto event) {
        eventValidator.validateEvent(event);
        return eventService.create(event);
    }

    @GetMapping("/{id}")
    public EventDto getEvent(@PathVariable @Positive Long id) {
        return eventService.getEvent(id);
    }

    @GetMapping
    public List<EventDto> getEventsByFilter(@RequestBody EventFilterDto filter) {
        return eventService.getEventsByFilter(filter);
    }

    @DeleteMapping("/{id}")
    public void deleteEvent(@PathVariable @Positive Long id) {
        eventService.deleteEvent(id);
    }

    @PutMapping
    public EventDto updateEvent(@NotNull @Validated(UpdateGroup.class) @RequestBody EventDto event) {
        eventValidator.validateEvent(event);
        return eventService.updateEvent(event);
    }

    @GetMapping("/{user_id}/owned_events")
    public List<Event> getOwnedEvents(@PathVariable("user_id") @Positive Long userId) {
        return eventService.getOwnedEvents(userId);
    }

    @GetMapping("/{user_id}/participated_events")
    public List<Event> getParticipatedEvents(@PathVariable("user_id") @Positive Long userId) {
        return eventService.getParticipatedEvents(userId);
    }
}
