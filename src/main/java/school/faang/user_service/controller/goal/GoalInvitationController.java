package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.service.GoalInvitationService;

@Component
@RequiredArgsConstructor
public class GoalInvitationController {
    private final GoalInvitationService invitationService;

    public GoalInvitationDto createInvitation(GoalInvitationDto invitationDto) {
        return invitationService.creatInvitation(invitationDto);
    }
}
