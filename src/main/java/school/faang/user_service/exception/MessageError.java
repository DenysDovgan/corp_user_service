package school.faang.user_service.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum MessageError {
    USER_DOES_NOT_EXIST("User does not exist"),
    SELF_FOLLOWING("Unable to follow/unfollow yourself"),
    FOLLOWING_EXISTS("Following already exists"),
    FOLLOWING_DOESNT_EXIST("Following does not exist"),
    ;

    private final String message;
}
