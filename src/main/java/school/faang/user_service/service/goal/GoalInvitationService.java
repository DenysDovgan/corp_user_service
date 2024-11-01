package school.faang.user_service.service.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import school.faang.user_service.dto.goal.GoalInvitationDto;
import school.faang.user_service.entity.RequestStatus;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.exeption.DataValidationException;
import school.faang.user_service.mapper.GoalInvitationMapper;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;
import school.faang.user_service.repository.goal.GoalRepository;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class GoalInvitationService {
    private final GoalInvitationMapper invitationMapper;
    private final GoalInvitationRepository goalInvitationRepository;
    private final UserRepository userRepository;
    private final GoalRepository goalRepository;
    private static final int MAX_GOALS = 3;

    @Transactional
    public GoalInvitationDto createInvitation(Long inviterId, Long invitedId, Long goalId, RequestStatus status) {
        if (inviterId == null || invitedId == null || goalId == null || status == null) {
            throw new DataValidationException("Arguments is null");
        }
        GoalInvitation invitation = new GoalInvitation();
        Optional<Goal> goal = goalRepository.findById(goalId);
        Optional<User> invitedUser = userRepository.findById(invitedId);
        Optional<User> inviterUser = userRepository.findById(inviterId);
        if (goal.isEmpty()) {
            throw new DataValidationException("Goal equals null");
        }
        if (invitation.getInviter() == invitation.getInvited() || invitedUser.isEmpty() || inviterUser.isEmpty()) {
            throw new DataValidationException("Users must be unequal and real");
        }
        invitation.setInvited(invitedUser.get());
        invitation.setInviter(inviterUser.get());
        invitation.setGoal(goal.get());
        invitation.setStatus(status);
        invitation = goalInvitationRepository.save(invitation);
        return invitationMapper.toDto(invitation);
    }

    public boolean acceptGoalInvitation(long id) {
        Optional<GoalInvitation> goalInvited = goalInvitationRepository.findById(id);
        if (goalInvited.isEmpty()) {
            throw new DataValidationException("GoalInvitation id not found");
        }

        GoalInvitation goalInvitation = goalInvited.get();
        User invitedUser = goalInvitation.getInvited();
        if (invitedUser.getGoals().size() >= MAX_GOALS) {
            rejectGoalInvitation(id);
            return false;
        }
        Optional<Goal> targetGoal = goalRepository.findById(goalInvitation.getGoal().getId());
        if (targetGoal.isEmpty()) {
            throw new IllegalArgumentException("Goal id not found");
        }
        invitedUser.getGoals().add(goalInvitation.getGoal());
        invitedUser.getReceivedGoalInvitations().add(goalInvitation);
        goalInvitation.setStatus(RequestStatus.ACCEPTED);

        goalInvitationRepository.save(goalInvitation);
        userRepository.save(invitedUser);
        return true;
    }

    public void rejectGoalInvitation(long id) {
        Optional<GoalInvitation> goalInvitationOptional = goalInvitationRepository.findById(id);
        if (goalInvitationOptional.isPresent()) {
            GoalInvitation goalInvitation = goalInvitationOptional.get();
            Optional<Goal> goal = goalRepository.findById(goalInvitation.getGoal().getId());
            if (goal.isEmpty()) {
                throw new DataValidationException("there is no such goal");
            }
            goalInvitation.setStatus(RequestStatus.REJECTED);
            goalInvitationRepository.save(goalInvitation);
        }
    }

    public List<Long> getInvitations(String patternInviter, String patternInvited,
                                     Long filterInviterById, Long filterInvitedById, RequestStatus status) {
        List<GoalInvitation> allGoalInvitations = goalInvitationRepository.findAll();
        if (patternInviter == null || patternInviter.isBlank()) {
            throw new DataValidationException("inviter состоит из одних пробелов");
        }
        return allGoalInvitations.stream()
                .filter(invitation -> invitation.getInviter() != null &&
                        invitation.getInviter().getUsername().equals(patternInviter))
                .filter(invitation -> patternInvited == null || patternInvited.isBlank() ||
                        (invitation.getInvited() != null && invitation.getInvited().getUsername().equals(patternInvited)))
                .filter(invitation -> filterInviterById == null || (invitation.getInviter().getId().equals(filterInviterById)))
                .filter(invitation -> filterInvitedById == null ||
                        (invitation.getInvited() != null && invitation.getInvited().getId().equals(filterInvitedById)))
                .filter(invitation -> status == null || invitation.getStatus().equals(status))
                .map(GoalInvitation::getId)
                .distinct() // если нужно, чтобы идентификаторы были уникальными
                .toList();
    }
}