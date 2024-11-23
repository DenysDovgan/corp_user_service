package school.faang.user_service.mapper.skill;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.recommendation.SkillDto;
import school.faang.user_service.entity.Skill;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SkillMapper {
    Skill toEntity(SkillDto skillDto);

    SkillDto toDto(Skill skill);

}
