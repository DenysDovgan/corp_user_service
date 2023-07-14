package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import school.faang.user_service.controller.event.EventParticipationController;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.service.event.EventParticipationService;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventParticipationControllerTest {
    @Mock
    private EventParticipationService eventParticipationService;

    @InjectMocks
    private EventParticipationController eventParticipationController;

    @Test
    public void checkValidateThrowsExceptionTest() {
        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipantController(null, null));
        assertThrows(DataValidationException.class,
                () -> eventParticipationController.registerParticipantController(-1L, 1L));
    }
}
