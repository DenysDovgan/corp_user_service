package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class RequestFilterDto {
    String description;
    Long requesterId;
    Long responderId;
    RequestStatus status;
}
