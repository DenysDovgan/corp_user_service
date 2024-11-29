package school.faang.user_service.service;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.mapper.user.UserMapper;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapper userMapper;

    private long userId;

    private User user;

    @BeforeEach
    void setUp() {
        userId = 1L;
        user = new User();
    }

    @Test
    public void testExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(true);
        assertTrue(userService.existsById(userId));
    }

    @Test
    public void testNotExistsUserById() {
        when(userRepository.existsById(userId)).thenReturn(false);
        assertFalse(userService.existsById(userId));
    }

    @Test
    public void testGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        assertEquals(user, userService.getUserById(userId));
    }

    @Test
    public void testThrowExceptionGetUserById() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUserByIdNotfound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    public void testGetUsersByIds() {
        List<User> users = List.of(new User(), new User());
        List<Long> ids = List.of(1L, 2L);

        when(userRepository.findAllById(ids)).thenReturn(users);
        userService.getUsersByIds(ids);
    }

    @Test
    public void testBanUser() {
        Long userId = 1L;
        User user = new User();
        user.setBanned(false);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.banUser(userId);

        assertTrue(user.isBanned());
        verify(userRepository, times(1)).save(user);
    }
}
