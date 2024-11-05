package school.faang.user_service.mapper.event;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.event.UserDto;
import school.faang.user_service.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserDto toDto(User user);
}
