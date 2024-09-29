package school.faang.user_service.controller.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.dto.subscription.responses.SuccessResponse;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.file.ValidFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping("/users/{userId}")
    UserDto getUser(@PathVariable @Positive long userId) {
        return userService.getUser(userId);
    }

    @PostMapping("/users")
    List<UserDto> getUsersByIds(@RequestBody @NotEmpty List<Long> ids) {
        return userService.getUsersByIds(ids);
    }

    @PostMapping("/users/upload")
    SuccessResponse uploadUser(@RequestParam("file") @ValidFile MultipartFile file) {
        userService.uploadUsers(file);
        return new SuccessResponse("The file is uploaded. Processing...");
    }
}
