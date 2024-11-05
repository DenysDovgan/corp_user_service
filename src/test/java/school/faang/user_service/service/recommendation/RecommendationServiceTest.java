package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import school.faang.user_service.dto.recommendation.RequestRecommendationDto;
import school.faang.user_service.dto.recommendation.SkillOfferDto;
import school.faang.user_service.dto.recommendation.ResponseRecommendationDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.Recommendation;
import school.faang.user_service.entity.recommendation.SkillOffer;
import school.faang.user_service.mapper.recommendation.RecommendationMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.recommendation.RecommendationRepository;
import school.faang.user_service.repository.recommendation.SkillOfferRepository;
import school.faang.user_service.validator.recommendation.RecommendationDtoValidator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {
    private static final long AUTHOR_ID = 1L;
    private static final long RECEIVER_ID = 2L;
    private static final long SKILL_ID = 1L;
    private static final long SKILL_OFFER_ID = 1L;
    private static final long RECOMMENDATION_ID = 1L;
    private static final String CONTENT = "content";
    private static final String SKILL_TITLE = "Java";

    @InjectMocks
    private RecommendationService recommendationService;

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private SkillOfferRepository skillOfferRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private RecommendationDtoValidator recommendationDtoValidator;

    @Mock
    private RecommendationMapper recommendationMapper;

    private Skill skill;
    private Recommendation recommendation;
    private RequestRecommendationDto requestRecommendationDto;
    private ResponseRecommendationDto responseRecommendationDto;
    private List<Recommendation> recommendations;
    private List<ResponseRecommendationDto> recommendationDtos;

    @BeforeEach
    public void setUp() {
        User author = User.builder().id(AUTHOR_ID).build();
        User receiver = User.builder().id(RECEIVER_ID).build();
        SkillOffer skillOffer = SkillOffer.builder().id(SKILL_OFFER_ID).build();
        skill = Skill.builder().id(SKILL_ID).build();

        SkillOfferDto skillOfferDto = SkillOfferDto.builder()
                .id(SKILL_OFFER_ID)
                .skillId(SKILL_ID)
                .skillTitle(SKILL_TITLE)
                .build();

        recommendation = Recommendation.builder()
                .id(RECOMMENDATION_ID)
                .author(author)
                .receiver(receiver)
                .content(CONTENT)
                .skillOffers(List.of(skillOffer))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        requestRecommendationDto = RequestRecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .skillOffers(List.of(skillOfferDto))
                .build();

        responseRecommendationDto = ResponseRecommendationDto.builder()
                .id(RECOMMENDATION_ID)
                .receiverId(RECEIVER_ID)
                .authorId(AUTHOR_ID)
                .content(CONTENT)
                .skillOffers(List.of(skillOfferDto))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        recommendations = List.of(new Recommendation(), new Recommendation());
        recommendationDtos = List.of(ResponseRecommendationDto.builder().build(),
                ResponseRecommendationDto.builder().build());
    }

    @Test
    @DisplayName("Test create recommendation with empty skill offers list")
    public void testCreateWhenSkillOffersListIsEmpty() {
        requestRecommendationDto.setSkillOffers(null);
        when(recommendationMapper.toEntity(requestRecommendationDto)).thenReturn(recommendation);

        assertThrows(DataValidationException.class, () -> recommendationService.create(requestRecommendationDto));

        verify(recommendationDtoValidator).validateRecommendation(requestRecommendationDto);
        verify(recommendationMapper).toEntity(requestRecommendationDto);
        verify(recommendationRepository, never()).save(recommendation);
    }

    @Test
    @DisplayName("Successful creation of a recommendation")
    public void testCreateThenSuccess() {
        when(recommendationMapper.toEntity(requestRecommendationDto)).thenReturn(recommendation);
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(recommendationMapper.toDto(recommendation)).thenReturn(responseRecommendationDto);
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));

        ResponseRecommendationDto resultRecommendationDto = recommendationService.create(requestRecommendationDto);

        assertNotNull(resultRecommendationDto);
        verify(recommendationDtoValidator).validateRecommendation(requestRecommendationDto);
        verify(recommendationMapper).toEntity(requestRecommendationDto);
        verify(recommendationRepository).save(eq(recommendation));
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    @DisplayName("Successful update of a recommendation")
    public void testUpdateThenSuccess() {
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));
        when(skillRepository.findById(SKILL_ID)).thenReturn(Optional.of(skill));
        when(recommendationRepository.save(any(Recommendation.class))).thenReturn(recommendation);
        when(recommendationMapper.toDto(recommendation)).thenReturn(responseRecommendationDto);

        doAnswer(invocation -> {
            RequestRecommendationDto dto = invocation.getArgument(0);
            recommendation.setContent(dto.getContent());
            recommendation.setReceiver(User.builder().id(dto.getReceiverId()).build());
            recommendation.setAuthor(User.builder().id(dto.getAuthorId()).build());
            return null;
        }).when(recommendationMapper).updateFromDto(requestRecommendationDto, recommendation);

        ResponseRecommendationDto result = recommendationService.update(RECOMMENDATION_ID, requestRecommendationDto);

        assertEquals(responseRecommendationDto, result);
        assertEquals(requestRecommendationDto.getContent(), recommendation.getContent());
        assertEquals(requestRecommendationDto.getAuthorId(), recommendation.getAuthor().getId());
        assertEquals(requestRecommendationDto.getReceiverId(), recommendation.getReceiver().getId());

        verify(recommendationDtoValidator).validateRecommendation(requestRecommendationDto);
        verify(recommendationRepository).save(recommendation);
        verify(recommendationMapper).toDto(recommendation);
    }

    @Test
    @DisplayName("Test update recommendation with empty skill offers list")
    public void testUpdateWhenSkillOffersListIsEmpty() {
        requestRecommendationDto.setSkillOffers(null);
        when(recommendationRepository.findById(RECOMMENDATION_ID)).thenReturn(Optional.of(recommendation));

        assertThrows(DataValidationException.class, () -> recommendationService.update(RECOMMENDATION_ID, requestRecommendationDto));
        verify(recommendationDtoValidator).validateRecommendation(requestRecommendationDto);
        verify(recommendationRepository, never()).save(recommendation);
    }

    @Test
    @DisplayName("Delete recommendation")
    void deleteRecommendation() {
        recommendationService.delete(RECOMMENDATION_ID);
        verify(recommendationRepository).deleteById(RECOMMENDATION_ID);
    }

    @Test
    @DisplayName("Successful receipt of all user recommendations")
    public void whenGetAllUserRecommendationsThenSuccess() {
        when(recommendationRepository.findAllByReceiverId(RECEIVER_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
        when(recommendationMapper.toDtoList(recommendations)).thenReturn(recommendationDtos);

        List<ResponseRecommendationDto> resultRecommendationDtos =
                recommendationService.getAllUserRecommendations(RECEIVER_ID);

        assertEquals(recommendationDtos, resultRecommendationDtos);
        verify(recommendationRepository).findAllByReceiverId(RECEIVER_ID, Pageable.unpaged());
        verify(recommendationMapper).toDtoList(recommendations);
    }

    @Test
    @DisplayName("Successful receipt of all the author's recommendations")
    public void whenGetAllGivenRecommendationsThenSuccess() {
        when(recommendationRepository.findAllByAuthorId(AUTHOR_ID, Pageable.unpaged()))
                .thenReturn(new PageImpl<>(recommendations, Pageable.unpaged(), recommendations.size()));
        when(recommendationMapper.toDtoList(recommendations)).thenReturn(recommendationDtos);

        List<ResponseRecommendationDto> resultRecommendationDtos =
                recommendationService.getAllGivenRecommendations(AUTHOR_ID);

        assertEquals(recommendationDtos, resultRecommendationDtos);
        verify(recommendationRepository).findAllByAuthorId(AUTHOR_ID, Pageable.unpaged());
        verify(recommendationMapper).toDtoList(recommendations);
    }
}