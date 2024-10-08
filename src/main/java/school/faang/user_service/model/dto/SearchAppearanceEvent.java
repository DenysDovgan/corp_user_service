package school.faang.user_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchAppearanceEvent {
    private Long userId;
    private Long actorId;
    private LocalDateTime receivedAt;
}