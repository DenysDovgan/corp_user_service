package school.faang.user_service.dto.recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillRequestDto {
    private Long id;
    private Long recommendationRequestId;
    private Long skillId;
    private String skillTitle;
}
