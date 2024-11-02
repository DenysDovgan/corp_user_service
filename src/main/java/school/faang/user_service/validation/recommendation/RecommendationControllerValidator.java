package school.faang.user_service.validation.recommendation;

import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationDto;
import school.faang.user_service.exception.DataValidationException;

@Component
public class RecommendationControllerValidator {

    public void validateDto(RecommendationDto recommendationDto) {

        if (recommendationDto.getAuthorId() == null || recommendationDto.getAuthorId() == 0) {
            throw new DataValidationException("Author id cannot be null");
        }

        if (recommendationDto.getAuthorId() < 0) {
            throw new DataValidationException("Author id cannot be negative");
        }

        if (recommendationDto.getReceiverId() == null || recommendationDto.getReceiverId() == 0) {
            throw new DataValidationException("Receiver id cannot be null");
        }

        if (recommendationDto.getReceiverId() < 0) {
            throw new DataValidationException("Receiver id cannot be negative");
        }

        if (recommendationDto.getReceiverId().equals(recommendationDto.getAuthorId())) {
            throw new DataValidationException("You cannot recommend yourself");
        }

        if (recommendationDto.getContent() == null || recommendationDto.getContent().isBlank()) {
            throw new DataValidationException("Recommendation cannot be empty");
        }

        if (recommendationDto.getSkillOffers() == null) {
            throw new DataValidationException("Skill offers list cannot be null");
        }
    }

    public void validateId(Long id) {
        if (id == null || id == 0) {
            throw new DataValidationException("Id cannot be null");
        }

        if (id <= 0) {
            throw new DataValidationException("Id cannot be negative");
        }
    }
}
