package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void checkUserExistenceReturnTrue() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);

        boolean result = userService.checkUserExistence(userId);

        assertTrue(result);
    }

    @Test
    void checkUserExistenceReturnFalse() {
        long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(false);

        boolean result = userService.checkUserExistence(userId);

        assertFalse(result);
    }

    @Test
    void testUserNotFound() {

        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> userService.findUserById(10L));
    }
}
