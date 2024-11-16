package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;
import school.faang.user_service.validator.recommendation.RecommendationRequestValidator;
import school.faang.user_service.validator.user.UserValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationRequestService {
    private final RecommendationRequestRepository recommendationRequestRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final UserValidator userValidator;
    private final RecommendationRequestValidator recommendationRequestValidator;
    private final RecommendationRequestMapper recommendationRequestMapper;
    private final List<RecommendationRequestFilter> recommendationRequestFilters;

    @Transactional
    public RecommendationRequestDto create(RecommendationRequestDto recommendationRequest) {
        log.info("Creating a recommendations request from user with id {} for user with id {}",
                recommendationRequest.getReceiverId(), recommendationRequest.getRequesterId());

        userValidator.validateUser(recommendationRequest.getRequesterId());
        userValidator.validateUser(recommendationRequest.getReceiverId());
        recommendationRequestValidator.validateRecommendation(recommendationRequest);

        RecommendationRequest request = recommendationRequestMapper.toEntity(recommendationRequest);
        request.getSkills().forEach(skillRequest ->
                skillRequestRepository.create(skillRequest.getId(), skillRequest.getSkill().getId()));
        request = recommendationRequestRepository.save(request);

        log.info("Recommendation request with id {} successfully saved", request.getId());
        return recommendationRequestMapper.toDto(request);
    }

    public List<RecommendationRequestDto> getRequests(RecommendationRequestFilterDto requestFilter) {
        Stream<RecommendationRequest> recommendationRequests = recommendationRequestRepository.findAll().stream();

        recommendationRequestFilters.stream()
                .filter(filter -> filter.isApplicable(requestFilter))
                .reduce(recommendationRequests,
                        (currentStream, filter) -> filter.apply(currentStream, requestFilter).stream(),
                        Stream::concat);


        log.info("Getting a list of recommendation requests after filtering");
        return recommendationRequestMapper.toDtoList(recommendationRequests.toList());
    }

    public RecommendationRequestDto getRequest(Long id) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        return recommendationRequestMapper.toDto(recommendationRequest);
    }

    public RejectionDto rejectRequest(Long id, RejectionDto rejectionDto) {
        RecommendationRequest recommendationRequest = recommendationRequestValidator.validateRecommendationFromBd(id);
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        recommendationRequest.setRejectionReason(rejectionDto.getRejectionReason());
        recommendationRequestRepository.save(recommendationRequest);

        log.info("Recommendation request with id {} was rejected", id);
        return recommendationRequestMapper.toRejectionDto(recommendationRequest);
    }
}
