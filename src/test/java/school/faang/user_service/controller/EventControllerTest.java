package school.faang.user_service.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.controller.event.EventController;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.event.Event;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class EventControllerTest {

    @InjectMocks
    private EventController eventController;

    @Mock
    private EventService eventService;

    @Test
    public void testCreateEventNullOrBlank() {
        EventDto eventDto = new EventDto();

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullStartDate() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Title1");

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testCreateWithNullOwnerId() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Title1");
        eventDto.setStartDate(LocalDateTime.of(2014, 9, 19, 14, 5));

        assertThrows(DataValidationException.class, () -> eventController.create(eventDto));
    }

    @Test
    public void testGetEvent() {
        Event event = new Event();
        event.setId(1L);

        eventController.getEvent(event.getId());
        verify(eventService, times(1)).getEvent(event.getId());
    }

    @Test
    public void testGetEventsByFilter() {
        EventFilterDto eventFilterDto = new EventFilterDto();

        eventController.getEventsByFilter(eventFilterDto);
        verify(eventService, times(1)).getEventsByFilter(eventFilterDto);
    }

    @Test
    public void testDeleteEvent() {
        Event event = new Event();
        event.setId(1L);

        eventController.deleteEvent(event.getId());
        verify(eventService, times(1)).deleteEvent(event.getId());
    }

    @Test
    public void testUpdateEvent() {
        EventDto eventDto = new EventDto();
        eventDto.setTitle("Title1");
        eventDto.setOwnerId(1L);
        eventDto.setStartDate(LocalDateTime.of(2014, 9, 19, 14, 5));

        eventController.updateEvent(eventDto);
        verify(eventService, times(1)).updateEvent(eventDto);
    }

    @Test
    public void testGetOwnedEvents() {
        User user = new User();
        user.setId(1L);

        eventController.getOwnedEvents(user.getId());
        verify(eventService, times(1)).getOwnedEvents(user.getId());
    }

    @Test
    public void testGetParticipatedEvents() {
        User user = new User();
        user.setId(1L);

        eventController.getParticipatedEvents(user.getId());
        verify(eventService, times(1)).getParticipatedEvents(user.getId());
    }
}