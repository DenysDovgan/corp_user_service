package school.faang.user_service.service.recommendation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import school.faang.user_service.dto.recommendation.RecommendationRequestDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestRejectionDto;
import school.faang.user_service.dto.recommendation.RecommendationRequestFilterDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.recommendation.RecommendationRequest;
import school.faang.user_service.entity.recommendation.SkillRequest;
import school.faang.user_service.mapper.recommendation.RecommendationRequestMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.recommendation.RecommendationRequestRepository;
import school.faang.user_service.repository.recommendation.SkillRequestRepository;
import school.faang.user_service.service.recommendation.filter.RecommendationRequestFilter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class RecommendationRequestServiceImplTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    private SkillRepository skillRepository;

    @MockBean
    private SkillRequestRepository skillRequestRepository;

    @Spy
    private RecommendationRequestMapper recommendationRequestMapper = Mappers.getMapper(RecommendationRequestMapper.class);

    @MockBean
    private List<RecommendationRequestFilter> recommendationRequestFilters;

    @Autowired
    private RecommendationRequestServiceImpl recommendationRequestService;

    @BeforeEach
    public void setUp() {
        Mockito.lenient().when(userRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new User()));
        Mockito.lenient().when(skillRepository.findUserSkill(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(new Skill()));
        Mockito.lenient().when(skillRepository.existsById(Mockito.anyLong())).thenReturn(true);
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(new RecommendationRequest()));
    }

    @Test
    public void requestedUserNotFound() {
        Mockito.lenient().when(userRepository.findById(getRecommendationRequestDto().getRequesterId())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(getRecommendationRequestDto()));
        assertEquals("Requester id %s not exist".formatted(getRecommendationRequestDto().getRequesterId()), exception.getMessage());
    }

    @Test
    public void receiverUserNotFound() {
        Mockito.lenient().when(userRepository.findById(getRecommendationRequestDto().getReceiverId())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(getRecommendationRequestDto()));
        assertEquals("Receiver id %s not exist".formatted(getRecommendationRequestDto().getReceiverId()), exception.getMessage());
    }

    @Test
    public void invalidLocalDateTimeCreateRequest() {
        RecommendationRequest recommendationRequest = new RecommendationRequest();
        recommendationRequest.setCreatedAt(LocalDateTime.of(2002, 3, 2, 1, 1));

        Mockito.lenient().when(recommendationRequestRepository.findLatestPendingRequest(Mockito.anyLong(), Mockito.anyLong())).thenReturn(Optional.of(recommendationRequest));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(getRecommendationRequestDto()));
        assertEquals("A recommendation request from the same user to another can be sent no more than once every 6 months.", exception.getMessage());
    }

    @Test
    public void receiverUserDontHaveSkill() {
        Mockito.lenient().when(skillRepository.findUserSkill(1L, getRecommendationRequestDto().getReceiverId())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(getRecommendationRequestDto()));
        assertEquals("The receiver user id %s does not have the skill %s".formatted(getRecommendationRequestDto().getReceiverId(), 1L), exception.getMessage());
    }

    @Test
    public void skillNotExists() {
        Mockito.lenient().when(skillRepository.existsById(1L)).thenReturn(false);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.create(getRecommendationRequestDto()));
        assertEquals("Skill id %s not exist".formatted(1L), exception.getMessage());
    }

    @Test
    public void createSuccess() {
        recommendationRequestService.create(getRecommendationRequestDto());
        Mockito.verify(recommendationRequestRepository).save(Mockito.any(RecommendationRequest.class));
    }

    @Test
    public void getRequestsNullFilter() {
        mockRecommendationRequestList();
        assertEquals(recommendationRequestMapper.toDto(getRecommendationRequestList()), recommendationRequestService.getRequests(null));
    }

    @Test
    public void getRequestsEmptyFilter() {
        mockRecommendationRequestList();
        assertEquals(List.of(), recommendationRequestService.getRequests(new RecommendationRequestFilterDto()));
    }

    @Test
    public void getRequestsStatusFilterPending() {
        mockRecommendationRequestList();
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(RequestStatus.PENDING, null, null);
        assertEquals(List.of(getRecommendationRequestDto(), getRecommendationRequestDto(), getRecommendationRequestDto()), recommendationRequestService.getRequests(recommendationRequestFilterDto));
    }

    @Test
    public void getRequestsStatusFilterAccepted() {
        mockRecommendationRequestList();
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(RequestStatus.ACCEPTED, null, null);
        RecommendationRequestDto recommendationRequestDtoAccepted = getRecommendationRequestDto();
        recommendationRequestDtoAccepted.setStatus(RequestStatus.ACCEPTED);
        assertEquals(List.of(recommendationRequestDtoAccepted), recommendationRequestService.getRequests(recommendationRequestFilterDto));
    }

    @Test
    public void getRequestsStatusFilterRejected() {
        mockRecommendationRequestList();
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(RequestStatus.REJECTED, null, null);
        RecommendationRequestDto recommendationRequestDtoRejected = getRecommendationRequestDto();
        recommendationRequestDtoRejected.setStatus(RequestStatus.REJECTED);
        assertEquals(List.of(recommendationRequestDtoRejected), recommendationRequestService.getRequests(recommendationRequestFilterDto));
    }

    @Test
    public void getRequestsStatusFilterRequester() {
        mockRecommendationRequestList();
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(null, 1L, null);
        assertEquals(getRecommendationRequestDtoList(), recommendationRequestService.getRequests(recommendationRequestFilterDto));
    }

    @Test
    public void getRequestsStatusFilterReceiver() {
        mockRecommendationRequestList();
        RecommendationRequestFilterDto recommendationRequestFilterDto = new RecommendationRequestFilterDto(null, null, 2L);
        assertEquals(getRecommendationRequestDtoList(), recommendationRequestService.getRequests(recommendationRequestFilterDto));
    }

    @Test
    public void getRequestInvalidId() {
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.getRequest(1L));
        assertEquals("Recommendation request id %s not found".formatted(1L), exception.getMessage());
    }

    @Test
    public void getRequestValidId() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendationRequest));
        assertEquals(recommendationRequestMapper.toDto(recommendationRequest), recommendationRequestService.getRequest(1L));
    }

    @Test
    public void rejectRequestAlreadyAccepted() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.ACCEPTED);
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendationRequest));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(1L, new RecommendationRequestRejectionDto("reason")));
        assertEquals("The recommendation request id %s is already accepted".formatted(recommendationRequest.getId()), exception.getMessage());
    }

    @Test
    public void rejectRequestAlreadyRejected() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.REJECTED);
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendationRequest));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(1L, new RecommendationRequestRejectionDto("reason")));
        assertEquals("The recommendation request id %s is already rejected".formatted(recommendationRequest.getId()), exception.getMessage());
    }

    @Test
    public void rejectRequestNotFound() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.PENDING);
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> recommendationRequestService.rejectRequest(1L, new RecommendationRequestRejectionDto("reason")));
        assertEquals("Recommendation request id %s not found".formatted(recommendationRequest.getId()), exception.getMessage());
    }

    @Test
    public void rejectRequestSuccess() {
        RecommendationRequest recommendationRequest = getRecommendationRequest();
        recommendationRequest.setStatus(RequestStatus.PENDING);
        Mockito.lenient().when(recommendationRequestRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(recommendationRequest));
        recommendationRequestService.rejectRequest(1L, new RecommendationRequestRejectionDto("reason"));
        Mockito.verify(recommendationRequestRepository).save(Mockito.any(RecommendationRequest.class));
    }

    private void mockRecommendationRequestList() {
        List<RecommendationRequest> requests = getRecommendationRequestList();
        Mockito.lenient().when(recommendationRequestRepository.findAll()).thenReturn(requests);
    }

    private RecommendationRequestDto getRecommendationRequestDto() {
        return new RecommendationRequestDto(1L, "message", RequestStatus.PENDING, null, List.of(1L), 1L, 2L, LocalDateTime.of(2002, 3, 2, 1, 1), LocalDateTime.of(2002, 3, 2, 1, 1));
    }

    private RecommendationRequest getRecommendationRequest() {
        RecommendationRequest recommendationRequest = recommendationRequestMapper.toEntity(getRecommendationRequestDto());
        User requester = new User();
        requester.setId(1L);

        User receiver = new User();
        receiver.setId(2L);

        Skill skill = new Skill();
        skill.setId(1L);

        recommendationRequest.setSkills(List.of(new SkillRequest(1L, recommendationRequest, skill)));
        recommendationRequest.setRequester(requester);
        recommendationRequest.setReceiver(receiver);
        return recommendationRequest;
    }

    private List<RecommendationRequest> getRecommendationRequestList() {
        RecommendationRequest recommendationRequestRejected = getRecommendationRequest();
        recommendationRequestRejected.setStatus(RequestStatus.REJECTED);
        RecommendationRequest recommendationRequestAccepted = getRecommendationRequest();
        recommendationRequestAccepted.setStatus(RequestStatus.ACCEPTED);
        return List.of(getRecommendationRequest(), getRecommendationRequest(), getRecommendationRequest(), recommendationRequestRejected, recommendationRequestAccepted);
    }

    private List<RecommendationRequestDto> getRecommendationRequestDtoList() {
        RecommendationRequestDto recommendationRequestDtoRejected = getRecommendationRequestDto();
        recommendationRequestDtoRejected.setStatus(RequestStatus.REJECTED);
        RecommendationRequestDto recommendationRequestDtoAccepted = getRecommendationRequestDto();
        recommendationRequestDtoAccepted.setStatus(RequestStatus.ACCEPTED);
        return List.of(getRecommendationRequestDto(), getRecommendationRequestDto(), getRecommendationRequestDto(), recommendationRequestDtoRejected, recommendationRequestDtoAccepted);
    }
}