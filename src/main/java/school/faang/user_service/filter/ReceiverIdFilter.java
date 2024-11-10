package school.faang.user_service.filter;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

@Component
public class ReceiverIdFilter implements Filter<RecommendationRequest, RequestFilterDto> {

    @Override
    public boolean isApplicable(RequestFilterDto filter) {
        return filter.getReceiverId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter) {
        Long receiverId = filter.getReceiverId();
        return stream.filter(request ->
                request.getReceiver() != null && receiverId.equals(request.getReceiver().getId())
        );
    }
}
