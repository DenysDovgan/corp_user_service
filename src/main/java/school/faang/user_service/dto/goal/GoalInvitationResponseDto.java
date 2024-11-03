package school.faang.user_service.dto.goal;

import school.faang.user_service.entity.RequestStatusDto;

import java.time.LocalDateTime;

public record GoalInvitationResponseDto(
        Long id,
        Long goalId,
        Long inviterId,
        Long invitedId,
        RequestStatusDto status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
