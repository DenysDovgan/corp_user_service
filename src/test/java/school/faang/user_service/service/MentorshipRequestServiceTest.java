package school.faang.user_service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestDto;
import school.faang.user_service.dto.mentorshipRequest.MentorshipRequestFilterDto;
import school.faang.user_service.dto.mentorshipRequest.RejectionDto;
import school.faang.user_service.entity.MentorshipRequest;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.filter.mentorshipRequestFilter.MentorshipRequestFilter;
import school.faang.user_service.mapper.mentorshipRequest.MentorshipRequestMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.mentorship.MentorshipRequestRepository;
import school.faang.user_service.validator.MentorshipRequestValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorshipRequestServiceTest {
    @InjectMocks
    private MentorshipRequestService mentorshipRequestService;
    @Mock
    private MentorshipRequestValidator mentorshipRequestValidator;
    @Mock
    private MentorshipRequestRepository mentorshipRequestRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MentorshipRequestMapper mentorshipRequestMapper = Mappers.getMapper(MentorshipRequestMapper.class);
    @Mock
    private List<MentorshipRequestFilter> mentorshipRequestFilterList;

    private MentorshipRequestDto mentorshipRequestDto;
    private MentorshipRequest mentorshipRequest;
    private User requester;
    private User receiver;

    @BeforeEach
    public void setUp() {

        requester = new User();
        requester.setId(1L);

        receiver = new User();
        receiver.setId(2L);

        mentorshipRequestDto = new MentorshipRequestDto();
        mentorshipRequestDto.setRequesterId(requester.getId());
        mentorshipRequestDto.setReceiverId(receiver.getId());
        mentorshipRequestDto.setDescription("Need mentorship on Java.");

        mentorshipRequest = new MentorshipRequest();
        mentorshipRequest.setRequester(requester);
        mentorshipRequest.setReceiver(receiver);
        mentorshipRequest.setStatus(RequestStatus.PENDING);
        mentorshipRequest.setDescription("Need mentorship on Java.");
    }

    @Test
    public void requestMentorshipTest_Success() {
        when(mentorshipRequestMapper.toEntity(mentorshipRequestDto)).thenReturn(mentorshipRequest);
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        assertNotNull(result);

        assertEquals(mentorshipRequestDto.getRequesterId(), result.getRequesterId());
        assertEquals(mentorshipRequestDto.getReceiverId(), result.getReceiverId());
        assertEquals(mentorshipRequestDto.getDescription(), result.getDescription());
        assertEquals(RequestStatus.PENDING, mentorshipRequest.getStatus());

        verify(mentorshipRequestRepository, times(1)).save(mentorshipRequest);
        verify(mentorshipRequestMapper, times(1)).toDto(mentorshipRequest);
    }

    @Test
    public void requestMentorshipTest_ValidationFailure() {
        doThrow(new DataValidationException("Ошибка валидации")).when(mentorshipRequestValidator).descriptionValidation(mentorshipRequestDto);
        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.requestMentorship(mentorshipRequestDto);
        });
        assertEquals("Ошибка валидации", exception.getMessage());

        verify(mentorshipRequestValidator, times(1)).descriptionValidation(mentorshipRequestDto);
        verify(mentorshipRequestValidator, never()).requesterReceiverValidation(any());
        verify(mentorshipRequestValidator, never()).selfRequestValidation(any());
        verify(mentorshipRequestValidator, never()).lastRequestDateValidation(any());
    }


    @Test
    public void getRequestsTest_NoFilters() {
        when(mentorshipRequestFilterList.isEmpty()).thenReturn(true);
        List<MentorshipRequestDto> result = mentorshipRequestService.getRequests(new MentorshipRequestFilterDto());
        assertTrue(result.isEmpty());
        verify(mentorshipRequestFilterList, times(1)).isEmpty();
    }

    @Test
    public void acceptRequestTest_Success() {
        requester.setMentors(new ArrayList<>());
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        mentorshipRequestService.acceptRequest(1L);
        assertEquals(1, requester.getMentors().size());
        assertTrue(requester.getMentors().contains(receiver));
    }

    @Test
    public void acceptRequestTest_UserAlreadyMentor() {
        requester.setMentors(List.of(receiver));
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));
        when(userRepository.findById(receiver.getId())).thenReturn(Optional.of(receiver));

        DataValidationException exception = assertThrows(DataValidationException.class, () -> {
            mentorshipRequestService.acceptRequest(1L);
        });

        assertEquals("Пользователь id2 уже является ментором отправителя id1", exception.getMessage());
    }

    @Test
    public void rejectRequestTest_Success() {
        mentorshipRequest.setStatus(RequestStatus.PENDING);

        when(mentorshipRequestRepository.findById(1L)).thenReturn(Optional.of(mentorshipRequest));
        when(mentorshipRequestMapper.toDto(mentorshipRequest)).thenReturn(mentorshipRequestDto);

        MentorshipRequestDto result = mentorshipRequestService.rejectRequest(1L, new RejectionDto("Not interested"));

        assertEquals(RequestStatus.REJECTED, mentorshipRequest.getStatus());
        assertEquals("Not interested", mentorshipRequest.getRejectionReason());
        verify(mentorshipRequestRepository, times(1)).findById(1L);
    }
}
