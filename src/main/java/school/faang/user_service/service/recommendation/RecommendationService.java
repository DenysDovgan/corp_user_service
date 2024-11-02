package school.faang.user_service.service.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.recommendation.RecommendationDto;
import school.faang.user_service.dto.skill.SkillOfferDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.validator.recommendation.RecommendationValidator;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final RecommendationRepository recommendationRepository;
    private final UserSkillGuaranteeRepository userSkillGuaranteeRepository;
    private final RecommendationValidator validator;
    private final RecommendationMapper mapper;

    @Transactional
    public RecommendationDto create(RecommendationDto recommendationDto) {
        User user = validator.validateUser(recommendationDto.getReceiverId());
        User author = validator.validateUser(recommendationDto.getAuthorId());
        validator.validateDate(recommendationDto);
        validator.validateSkills(recommendationDto);

        setGuarantee(recommendationDto, author, user);
        Recommendation savedRecommendation = recommendationRepository.save(mapper.toRecommendation(recommendationDto));
        return mapper.toRecommendationDto(savedRecommendation);
    }

    private void setGuarantee(RecommendationDto recommendationDto, User author, User user) {
        List<Skill> skills = user.getSkills();
        if (recommendationDto.getSkillOffers() == null){
            return;
        }
        for (SkillOfferDto offer : recommendationDto.getSkillOffers()) {
            for (Skill skill : skills) {
                if (skill.getId() == offer.getSkillId()){
                    UserSkillGuarantee skillGuarantee = UserSkillGuarantee.builder()
                            .skill(skill)
                            .guarantor(author)
                            .user(user)
                            .build();
                    userSkillGuaranteeRepository.save(skillGuarantee);
                }
            }
        }
    }
}
