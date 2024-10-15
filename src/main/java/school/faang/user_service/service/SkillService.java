package school.faang.user_service.service;

import jakarta.transaction.Transactional;
import school.faang.user_service.model.dto.SkillCandidateDto;
import school.faang.user_service.model.dto.SkillDto;

import java.util.List;

public interface SkillService {
    @Transactional
    SkillDto create(SkillDto skillDto);

    List<SkillDto> getUserSkills(long userId);

    List<SkillCandidateDto> getOfferedSkills(long userId);

    @Transactional
    SkillDto acquireSkillFromOffers(long skillId, long userId);
}
