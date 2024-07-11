package school.faang.user_service.service.event;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.mapper.UserMapper;
import school.faang.user_service.repository.event.EventParticipationRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EventParticipationService {
    private final EventParticipationRepository eventParticipationRepository;
    private final UserMapper userMapper;

    private boolean hasAnyParticipant(long eventId, long userId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .anyMatch(user -> user.getId() == userId);
    }

    public void registerParticipant(long eventId, long userId) {
        if (!hasAnyParticipant(eventId, userId)) {
            eventParticipationRepository.register(eventId, userId);
        } else {
            throw new RuntimeException("User " + userId + " is already registered on event " + eventId);
        }
    }

    public void unregisterParticipant(long eventId, long userId) {
        if (hasAnyParticipant(eventId, userId)) {
            eventParticipationRepository.unregister(eventId, userId);
        } else {
            throw new RuntimeException("User " + userId + " is not registered on event " + eventId);
        }
    }

    public List<UserDto> getParticipant(long eventId) {
        return eventParticipationRepository.findAllParticipantsByEventId(eventId).stream()
                .map(userMapper::toDto)
                .toList();
    }

    public int getParticipantsCount(long eventId) {
        return eventParticipationRepository.countParticipants(eventId);
    }
}
