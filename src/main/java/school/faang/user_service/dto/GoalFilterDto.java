package school.faang.user_service.dto;

import lombok.Data;
import school.faang.user_service.entity.goal.GoalStatus;

@Data
public class GoalFilterDto {
    private String title;
    private String description;
    private GoalStatus status;
}
