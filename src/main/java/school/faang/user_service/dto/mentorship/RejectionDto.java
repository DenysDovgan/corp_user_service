package school.faang.user_service.dto.mentorship;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RejectionDto {

    private Long id;
    private String reason;
    private Long requesterId;
    private Long receiverId;

}
