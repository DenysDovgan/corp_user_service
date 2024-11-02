package school.faang.user_service.dto.mentorship;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String username;
    private String email;
    private String aboutMe;
    private String phone;
    private Integer experience;
    private String city;

}
