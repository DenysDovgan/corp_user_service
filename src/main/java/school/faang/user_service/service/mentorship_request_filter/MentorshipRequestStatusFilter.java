package school.faang.user_service.service.mentorship_request_filter;

import lombok.Data;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.MentorshipRequest;

import java.util.List;
import java.util.Objects;

@Component
@Data
public class MentorshipRequestStatusFilter implements MentorshipRequestFilter {
    @Override
    public boolean isApplecable(RequestFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public List<MentorshipRequest> apply(List<MentorshipRequest> requests, RequestFilterDto filters) {
        return requests.stream()
                .filter(request -> Objects.equals(request.getStatus(), filters.getStatus()))
                .toList();
    }
}
