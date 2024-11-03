package school.faang.user_service.dto.mentorship;

import lombok.Data;
import school.faang.user_service.entity.RequestStatus;

@Data
public class MentorshipRequestDto {

  private Long id;
  private String description;
  private Long requesterId;
  private Long receiverId;
  private RequestStatus status;
  private String rejectionReason;
  private String createdAt;
  private String updatedAt;
}
