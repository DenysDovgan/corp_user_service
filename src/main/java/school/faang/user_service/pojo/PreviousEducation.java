
package school.faang.user_service.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreviousEducation {
    private String degree;
    private String institution;
    private Integer completionYear;
}
