package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.RecommendationEvent;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RecommendationRequestMapper {
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "skillsId", source = "skills", qualifiedByName = "skillsToIds")
    RecommendationRequestDto toDto(RecommendationRequest recommendationRequest);

    @Mapping(target = "recommendationId", source = "id")
    @Mapping(target = "requesterId", source = "requester.id")
    @Mapping(target = "receiverId", source = "receiver.id")
    @Mapping(target = "eventTime", source = "updatedAt")
    RecommendationEvent toEvent(RecommendationRequest recommendationRequest);

    @Named("skillsToIds")
    default List<Long> skillsToIds(List<SkillRequest> skills) {
        if (skills == null) {
            return null;
        }

        return skills.stream()
                .map(SkillRequest::getId)
                .toList();
    }


    RecommendationRequest toEntity(RecommendationRequestDto recommendationRequestDto);
}
