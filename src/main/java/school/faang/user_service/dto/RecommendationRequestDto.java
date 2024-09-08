package school.faang.user_service.dto;

import lombok.Builder;
import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record RecommendationRequestDto(
        Long id,
        String message,
        RequestStatus status,
        List<Long> skillsId,
        long requesterId,
        long receiverId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
