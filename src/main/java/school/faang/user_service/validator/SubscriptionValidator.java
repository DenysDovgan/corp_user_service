package school.faang.user_service.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.SubscriptionRepository;

@Component
@RequiredArgsConstructor
public class SubscriptionValidator {

    private final SubscriptionRepository subscriptionRepository;

    public void validateFollowUser(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("User ID must be positive.");
        }
        if (subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription already exists.");
        }
    }

    public void validateUnfollowUser(long followerId, long followeeId) {
        if (followerId <= 0 || followeeId <= 0) {
            throw new DataValidationException("User ID must be positive.");
        }
        if (!subscriptionRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            throw new DataValidationException("Subscription does not exist.");
        }
    }

    public void validateUserExists(long userId) {
        if (userId <= 0) {
            throw new DataValidationException("User ID must be positive.");
        }
        if (!subscriptionRepository.existsById(userId)) {
            throw new DataValidationException("User does not exist.");
        }
    }
}
