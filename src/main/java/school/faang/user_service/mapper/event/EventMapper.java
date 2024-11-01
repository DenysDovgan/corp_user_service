package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.event.EventDto;
import school.faang.user_service.entity.event.Event;

@Component
@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventDto eventDto);

    EventDto toDto(Event event);
}