package school.faang.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import school.faang.user_service.entity.contact.PreferredContact;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private String phone;
    private String aboutMe;
    private String city;
    private String country;
    private String password;
    private String telegramChatId;
    private Integer experience;
    private PreferredContact preference;

}