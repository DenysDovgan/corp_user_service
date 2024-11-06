package school.faang.user_service.filter;

import school.faang.user_service.dto.RequestFilterDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;

import java.util.stream.Stream;

public interface RequestFilter {

    boolean isApplicable(RequestFilterDto filters);

    Stream<RecommendationRequest> apply(Stream<RecommendationRequest> stream, RequestFilterDto filter);
}
