package school.faang.user_service.service.event.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventMaxAttendeesFilter implements EventFilter {

    @Override
    public boolean isApplicable(EventFilterDto filters) {
        return filters.getMaxAttendees() != 0;
    }

    @Override
    public void apply(Stream<Event> events, EventFilterDto filters) {
        events.filter(event -> event.getMaxAttendees() == filters.getMaxAttendees());
    }
}
