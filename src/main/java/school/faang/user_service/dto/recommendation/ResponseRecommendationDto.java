package school.faang.user_service.dto.recommendation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ResponseRecommendationDto {
    private Long id;
    private Long authorId;
    private Long receiverId;
    private String content;
    private List<SkillOfferDto> skillOffers;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
