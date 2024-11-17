package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.SkillCandidateDto;
import school.faang.user_service.dto.SkillDto;
import school.faang.user_service.dto.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.mapper.SkillMapper;
import school.faang.user_service.exceptions.ResourceNotFoundException;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {
    private static final long MIN_SKILL_OFFERS = 3;
    private final SkillRepository skillRepo;
    private final SkillMapper skillMapper;
    private final SkillOfferRepository skillOfferRepo;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepo;
    private final UserRepository userRepo;

    public Skill getSkillById(Long id) {
        return skillRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", "id", id));
    }

    public List<Skill> getSkillsFrom(List<SkillOfferDto> skillOffers) {
        return skillOffers.stream()
                .map(skillOfferDto -> getSkillById(skillOfferDto.skillId()))
                .toList();
    }

    public List<Skill> getAllSkillsByIds(List<Long> skillIds) {
        return skillRepo.findAllById(skillIds);
    }

    public SkillDto create(SkillDto skillDto) {
        if (skillRepo.existsByTitle(skillDto.title())) {
            throw new DataValidationException("Couldn't create a skill:"
                    + " skill already exists");
        }
        Skill skill = skillMapper.toEntity(skillDto);
        skill = skillRepo.save(skill);
        return skillMapper.toDto(skill);
    }

    public List<SkillDto> getUserSkills(Long userId) {
        List<Skill> allSkills = skillRepo.findAllByUserId(userId);
        return allSkills.stream().map(skillMapper::toDto).toList();
    }

    public List<SkillCandidateDto> getOfferedSkills(Long userId) {
        List<Skill> allOfferedSkills = skillRepo.findSkillsOfferedToUser(userId);
        Map<Skill, Long> sortedMap = allOfferedSkills.stream()
                .collect(Collectors.groupingBy(skill -> skill,
                        Collectors.counting()));
        List<SkillCandidateDto> sortedOfferedSkills = sortedMap.entrySet()
                .stream()
                .map(entryPair -> SkillCandidateDto.builder()
                        .skillDto(skillMapper.toDto(entryPair.getKey()))
                        .offersAmount(entryPair.getValue())
                        .build())
                .toList();
        return sortedOfferedSkills;
    }

    public SkillDto acquireSkillFromOffers(Long skillId, Long userId) {
        Optional<Skill> offeredSkill = skillRepo.findUserSkill(skillId, userId);
        if (offeredSkill.isEmpty()) {
            assignSkillIfCountValid(skillId, userId);
            createAndAddGuarantee(skillId, userId);
            Skill skill = skillRepo.findById(skillId).orElseThrow(() ->
                    new EntityNotFoundException("Skill not found"));
            skillRepo.save(skill);
            return skillMapper.toDto(skill);
        } else {
            throw new DataValidationException("Skill with " + skillId
                    + " id is already exists");
        }
    }

    private void assignSkillIfCountValid(Long skillId, Long userId) {
        List<SkillOffer> allSkillOffers =
                skillOfferRepo.findAllOffersOfSkill(skillId, userId);
        long count = allSkillOffers.size();
        if (count >= MIN_SKILL_OFFERS) {
            skillRepo.assignSkillToUser(skillId, userId);
        } else {
            throw new RuntimeException("Not enough offers of skill" +
                    " to assign to user");
        }
    }

    private void createAndAddGuarantee(Long skillId, Long userId) {
        List<SkillOffer> allSkillOffers =
                skillOfferRepo.findAllOffersOfSkill(skillId, userId);
        Skill skill = skillRepo.findById(skillId).orElseThrow(() ->
                new EntityNotFoundException("Skill not found"));
        User user = userRepo.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User not found"));
        for (SkillOffer skillOffer : allSkillOffers) {
            UserSkillGuarantee newGuarantee = UserSkillGuarantee.builder()
                    .user(user)
                    .skill(skill)
                    .guarantor(skillOffer.getRecommendation().getAuthor())
                    .build();
            userSkillGuaranteeRepo.save(newGuarantee);
            skill.addGuarantee(newGuarantee);
        }
    }
}
