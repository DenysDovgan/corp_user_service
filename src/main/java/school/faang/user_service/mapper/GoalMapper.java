package school.faang.user_service.mapper;

import org.mapstruct.*;
import org.mapstruct.factory.Mappers;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.goal.Goal;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GoalMapper {
    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);

    @Mapping(source = "parent.id", target = "parentId")
    @Mapping(source = "skillsToAchieve", target = "skillIds", qualifiedByName = "mapSkillsToSkillIdsMapper")
    GoalDto toDto(Goal goal);

    @Mapping(target = "parent.id", source = "parentId")
    @Mapping(target = "skillsToAchieve", ignore = true)
    Goal toEntity(GoalDto goalDto);

    @Named("mapSkillsToSkillIdsMapper")
    default List<Long> mapSkillsToSkillIds(List<Skill> skills) {
        return skills.stream()
                .map(Skill::getId)
                .collect(Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    Goal updateFromDto(GoalDto dto, @MappingTarget Goal goal);
}