package school.faang.user_service.controller.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import school.faang.user_service.service.goal.GoalInvitationService;

/**
 * @author Alexander Bulgakov
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/goal-invitation")
public class GoalInvitationController {

    private final GoalInvitationService goalInvitationService;

    @PostMapping
    public void acceptGoalInvitation(long id) {
        goalInvitationService.acceptGoalInvitation(id);
    }
}
