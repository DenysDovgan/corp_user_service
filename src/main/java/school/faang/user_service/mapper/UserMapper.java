package school.faang.user_service.mapper;

import org.mapstruct.Mapper;

import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toDto(User user);
    User toEntity(UserDto userDto);

    List<UserDto> toDto(List<User> users);
    List<User> toEntity(List<UserDto> userDtos);
}
