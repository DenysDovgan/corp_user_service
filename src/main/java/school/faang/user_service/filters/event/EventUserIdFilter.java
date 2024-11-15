package school.faang.user_service.filters.event;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventFilterDto;
import school.faang.user_service.entity.event.Event;

import java.util.stream.Stream;

@Component
public class EventUserIdFilter implements EventFilter {
    @Override
    public boolean isApplicable(EventFilterDto eventFilterDto) {
        return eventFilterDto.getUserId() != null;
    }

    @Override
    public Stream<Event> apply(Stream<Event> events, EventFilterDto filterDto) {
        return events
                .filter(event -> event.getOwner().getId().equals(filterDto.getUserId()));
    }
}