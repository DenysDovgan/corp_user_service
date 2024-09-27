package school.faang.user_service.controller;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.service.RecommendationService;

import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/recommendations")
@Validated
public class RecommendationController {
    private final RecommendationService recommendationService;

    @PostMapping
    public RecommendationDto giveRecommendation(@RequestBody RecommendationDto recommendation) {
        return recommendationService.create(recommendation);
    }

    @PutMapping
    public RecommendationDto updateRecommendation(@RequestBody RecommendationDto updated) {
        return recommendationService.updateRecommendation(updated);
    }

    @DeleteMapping("/{id}")
    public RecommendationDto deleteRecommendation(@Positive @PathVariable long id) {
        return recommendationService.delete(id);
    }

    @GetMapping("/receiver/{receiverId}")
    public List<RecommendationDto> getAllUserRecommendations(@Positive @PathVariable long receiverId) {
        return recommendationService.getAllUserRecommendations(receiverId);
    }

    @GetMapping("/author/{authorId}")
    public List<RecommendationDto> getAllGivenRecommendations(@Positive @PathVariable long authorId) {
        return recommendationService.getAllGivenRecommendations(authorId);
    }
}
