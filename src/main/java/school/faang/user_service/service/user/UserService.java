package school.faang.user_service.service.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import school.faang.user_service.entity.User;
import school.faang.user_service.exception.DataValidationException;
import school.faang.user_service.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    UserRepository userRepository;

    public User findUserById(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataValidationException("User not found"));
    }
}