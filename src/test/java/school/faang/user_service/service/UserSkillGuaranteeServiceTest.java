package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.TestObjectGenerator;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.UserSkillGuarantee;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.repository.UserSkillGuaranteeRepository;
import school.faang.user_service.validation.recommendation.RecommendationServiceValidator;
import school.faang.user_service.validation.skill.SkillValidation;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserSkillGuaranteeServiceTest {

    @Mock
    private UserSkillGuaranteeRepository userSkillGuaranteeRepository;

    @Mock
    private SkillValidation skillValidation;

    @Mock
    private UserService userService;

    @Mock
    private RecommendationServiceValidator recommendationServiceValidator;

    @InjectMocks
    private UserSkillGuaranteeService userSkillGuaranteeService;

    private TestObjectGenerator testObjectGenerator = new TestObjectGenerator();
    private UserSkillGuarantee userSkillGuarantee;
    private User user;
    private Skill skill;
    private Recommendation recommendation;

    @BeforeEach
    public void setUp() {
        user = testObjectGenerator.createUserTest();
        skill = testObjectGenerator.createSkillTest();
        recommendation = testObjectGenerator.createRecommendationTest();
        userSkillGuarantee = testObjectGenerator.createUserSkillGuaranteeTest();
    }

    @Test
    void testAddSkillGuarantee() {
        when(userService.findUserById(recommendation.getReceiver().getId())).thenReturn(user);

        userSkillGuaranteeService.addSkillGuarantee(skill, recommendation);

        verify(recommendationServiceValidator, times(1)).validateRecommendationExistsById(recommendation.getId());
        verify(skillValidation, times(1)).validateSkillExists(skill.getId());
        verify(userSkillGuaranteeRepository, times(1)).save(any(UserSkillGuarantee.class));
    }

}
