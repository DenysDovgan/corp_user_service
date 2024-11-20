package school.faang.user_service.filter.recommendationRequest;

import lombok.Builder;
import school.faang.user_service.dto.recommendationRequest.RecommendationRequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.Objects;
import java.util.stream.Stream;

@Builder
public class RecommendationRequestRequesterFilter implements RecommendationRequestFilter {

    @Override
    public boolean isApplicable(RecommendationRequestFilterDto filterDto) {
        return filterDto.getRequesterId() != null;
    }

    @Override
    public Stream<RecommendationRequest> apply(Stream<RecommendationRequest> recommendationRequests, RecommendationRequestFilterDto filterDto) {
        return recommendationRequests.filter(request -> Objects.equals(request.getRequester().getId(), filterDto.getRequesterId()));
    }
}
