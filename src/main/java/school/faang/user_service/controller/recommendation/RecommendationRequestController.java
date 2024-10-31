package school.faang.user_service.controller.recommendation;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RequestFilterDto;
import school.faang.user_service.service.recommendation.RecommendationRequestService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class RecommendationRequestController {
    private final RecommendationRequestService recommendationRequestService;

    @PostMapping("/recommendation-requests/create")
    public RecommendationRequestDto requestRecommendation(@RequestBody RecommendationRequestDto recommendationRequestDto) {
        return recommendationRequestService.create(recommendationRequestDto);
    }

    @PostMapping("/recommendation-requests/filter")
    public List<RecommendationRequestDto> getRecommendationRequests(@RequestBody RequestFilterDto filterDto) {
        return recommendationRequestService.getRequests(filterDto);
    }

    @GetMapping("/recommendation-requests/{id}")
    public RecommendationRequestDto getRecommendationRequest(@PathVariable Long id) {
        return recommendationRequestService.getRequest(id);
    }

    @PostMapping("/recommendation-requests/{id}/reject")
    public RejectionDto rejectRequest(@PathVariable Long id, @RequestBody RejectionDto rejectionDto) {
        return recommendationRequestService.rejectRequest(id, rejectionDto);
    }
}
