package school.faang.user_service.mapper;

import org.mapstruct.Mapper;
import school.faang.user_service.dto.CountDto;
import org.mapstruct.ReportingPolicy;
import school.faang.user_service.dto.UserDto;
import school.faang.user_service.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    UserDto toUserDto(User user);

    List<UserDto> toUsersDtos(List<User> users);

    CountDto toCountDto(Integer count);
}

