package school.faang.user_service.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.user.UserDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.exception.EntityNotFoundException;
import school.faang.user_service.mapper.UserMapperImpl;
import school.faang.user_service.repository.UserRepository;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Spy
    private UserMapperImpl userMapper = new UserMapperImpl();
    @Mock
    private S3Service s3Service;
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserService userService;

    @Test
    void getUser_whenUserExists_thenReturnUserDto() {
        long userId = 1L;
        User user = new User();
        user.setUsername("Bob");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(userId);

        assertNotNull(result);
        assertEquals("Bob", result.getUsername());
    }

    @Test
    void getUser_whenUserNotExists_getUserThrowsException() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        DataValidationException dataValidationException = assertThrows(DataValidationException.class, () -> userService.getUser(userId));

        assertEquals("Пользователя не существует", dataValidationException.getMessage());
    }

    @Test
    void getUserById_whenUserIdExist_thenReturnUser() {
        // Arrange
        long userId = 1;
        User user = User.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        userService.getUserById(userId);

        // Assert
        verify(userRepository, times(1)).findById(userId);
        assertEquals(user.getId(), userId);
    }

    @Test
    void testGetUserById_whenUserIdNotExist_thenThrowEntityNotFoundException() {
        long userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(userId));
    }
    @Test
    void createUser_saveUserinBd() {
        // Arrange
        UserDto userDto = UserDto.builder()
                .username("Elvis")
                .email("email")
                .password("password")
                .phone("12345")
                .countryId(4L).build();
        byte[] file = new byte[1];
        when(restTemplate.getForObject((String) any(), eq(byte[].class))).thenReturn(file);
        when(s3Service.uploadFile(file, userDto.getUsername())).thenReturn(new UserProfilePic());

        // Act
        userService.createUser(userDto);

        // Assert
        assertAll(
                () -> verify(userMapper, times(1)).toEntity(userDto),
                () -> verify(s3Service, times(1)).uploadFile(any(), any()),
                () -> verify(userMapper, times(1)).toDto((User) any()),
                () -> verify(userRepository, times(1)).save((User) any())
        );
    }

}