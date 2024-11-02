package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.entity.recommendation.Recommendation;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RecommendationValidator {

    public boolean isPeriodElapsedSinceLastRecommendation(Recommendation oldRecommendation,
                                                          Recommendation newRecommendation,
                                                          Duration requiredDuration) {
        if (oldRecommendation == null) {
            return true;
        }
        Duration durationBetween = Duration.between(oldRecommendation.getUpdatedAt(),
                newRecommendation.getCreatedAt());
        return durationBetween.compareTo(requiredDuration) > 0;
    }
}
