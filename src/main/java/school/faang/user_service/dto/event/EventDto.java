package school.faang.user_service.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import reactor.util.annotation.Nullable;
import school.faang.user_service.dto.skill.SkillDto;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDto {

    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 64, message = "title size more than 64 symbol")
    private String title;

    @NotNull
    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd.MM.yyyy HH:mm:ss")
    @NotNull
    private LocalDateTime endDate;

    @NotNull
    private Long ownerId;

    @NotNull
    @Size(max = 4096, message = "message length more than 4096 symbol")
    private String description;

    @NotNull
    private List<SkillDto> relatedSkills;

    @Nullable
    @Size(max = 128, message = "location length more than 128 symbol")
    private String location;

    private int maxAttendees = 0;
}