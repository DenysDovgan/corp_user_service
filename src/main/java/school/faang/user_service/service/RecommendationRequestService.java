package school.faang.user_service.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.RecommendationRequestDto;
import school.faang.user_service.dto.RejectionDto;
import school.faang.user_service.dto.SkillRequestDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.filter.recommendation.RequestFilter;
import school.faang.user_service.service.filter.recommendation.RequestFilterDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;


@Component
@RequiredArgsConstructor
@Slf4j
public class RecommendationRequestService {

    private static final int HALF_YEAR = 6;

    private final RecommendationRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final SkillRequestRepository skillRequestRepository;
    private final SkillRepository skillRepository;
    private final RecommendationRequestMapper mapper;
    private final List<RequestFilter> requestFilters;

    public RecommendationRequestDto create(RecommendationRequestDto dto) {
        validateRecommendationRequestDto(dto);

        dto.skills().forEach(skill -> skillRequestRepository.create(skill.requestId(), skill.skillId()));

        RecommendationRequest request = requestRepository.save(mapper.toEntity(dto));
        RecommendationRequestDto returnDto = mapper.toDto(request);
        log.info("Create request with id = " + returnDto.id() + ", requesterId = " + returnDto.requesterId() +
                ", receiverId = " + returnDto.receiverId());
        return returnDto;
    }

    public List<RecommendationRequestDto> getRequests(RequestFilterDto filterDto) {
        List<RecommendationRequest> recommendationRequests = requestRepository.findAll();
        Stream<RecommendationRequest> stream = recommendationRequests.stream();
        List<RequestFilter> filters = requestFilters.stream()
                .filter(flt -> flt.isApplicable(filterDto))
                .toList();

        for (RequestFilter filter : filters) {
            stream = filter.apply(filterDto, stream);
        }
        List<RecommendationRequest> requests = stream.toList();
        List<Long> requestIds = requests.stream()
                        .map(RecommendationRequest::getId)
                        .toList();
        log.info("Returning filtered requests: " + requestIds);
        return mapper.toDto(requests);
    }

    public RecommendationRequestDto getRequest(long id) {
        RecommendationRequest request = requestRepository.findById(id).orElse(null);
        if (request != null) {
            log.info("Returning request with id = " + request.getId());
        } else {
            log.info("Returning empty request");
        }
        return mapper.toDto(request);
    }

    public void rejectRequest(long id, RejectionDto rejection) {
        Optional<RecommendationRequest> requestOpt = requestRepository.findById(id);
        if (requestOpt.isEmpty()) {
            throw new DataValidationException("Запрашиваемого запроса нет в базе данных");
        }
        RecommendationRequest request = requestOpt.get();
        if (request.getStatus() == RequestStatus.REJECTED) {
            throw new DataValidationException("Запрос уже был отклонён");
        }
        if (request.getStatus() == RequestStatus.ACCEPTED) {
            throw new DataValidationException("Запрос уже принят, нельзя отклонить принятый запрос");
        }

        request.setStatus(RequestStatus.REJECTED);
        request.setRejectionReason(rejection.reason());
        log.info("Rejected request with id =  " + request.getId() + " and reason = " + request.getRejectionReason());
        requestRepository.save(request);
    }

    private void validateRecommendationRequestDto(RecommendationRequestDto dto) {
        Optional<User> requester = userRepository.findById(dto.requesterId());
        if (requester.isEmpty()) {
            throw new DataValidationException("Requester отсутствует в базе данных");
        }
        Optional<User> receiver = userRepository.findById(dto.receiverId());
        if (receiver.isEmpty()) {
            throw new DataValidationException("Receiver отсутствует в базе данных");
        }
        Optional<RecommendationRequest> lastRequest = requestRepository.findLatestPendingRequest(
                dto.requesterId(),
                dto.receiverId()
        );
        if (lastRequest.isPresent() && LocalDateTime.now().isBefore(lastRequest.get().getCreatedAt().plusMonths(HALF_YEAR))) {
            throw new DataValidationException("Запрос рекомендации можно отправлять не чаще, чем один раз в "+ HALF_YEAR + " месяцев");
        }
        List<SkillRequestDto> skillRequestDtos = dto.skills();
        if (skillRequestDtos == null || skillRequestDtos.isEmpty()) {
            throw new DataValidationException("Скиллы отсутствуют в запросе");
        }
        List<Long> skillIds = skillRequestDtos.stream()
                .map(SkillRequestDto::skillId)
                .toList();
        int countExistsSkill = skillRepository.countExisting(skillIds);
        if (countExistsSkill != skillIds.size()) {
            throw new DataValidationException("Не все скиллы существуют в базе данных");
        }
    }
}
