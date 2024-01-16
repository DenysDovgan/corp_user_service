package school.faang.user_service.service.goal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalInvitation;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalInvitationRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * @author Alexander Bulgakov
 */

@ExtendWith(MockitoExtension.class)
public class GoalInvitationServiceTest {
 @Mock
 private GoalInvitationRepository goalInvitationRepository;
 @Mock
 private UserRepository userRepository;
 @InjectMocks
 private GoalInvitationService goalInvitationService;


 @Test
 @DisplayName("Test accept goal invitation")
 public void acceptGoalInvitation_shouldAddInvitationToUserGoals() {
  long id = 1L;

  User invitedUser = new User();
  invitedUser.setId(1L);

  GoalInvitation goalInvitation = new GoalInvitation();
  goalInvitation.setId(id);
  goalInvitation.setInvited(invitedUser);

  Goal goal = new Goal();
  goal.setId(1L);

  List<Goal> goals = new ArrayList<>();
  goals.add(goal);

  invitedUser.setGoals(goals);

  when(goalInvitationRepository.findById(id)).thenReturn(Optional.of(goalInvitation));
  when(userRepository.existsById(invitedUser.getId())).thenReturn(true);
  when(goalInvitationRepository.existsById(goalInvitation.getId())).thenReturn(true);

  goalInvitationService.acceptGoalInvitation(id);

  assertTrue(invitedUser.getReceivedGoalInvitations().contains(goalInvitation));
  assertTrue(invitedUser.getGoals().contains(goalInvitation.getGoal()));
 }

 @Test
 @DisplayName("Test accept goal invitation when user not found")
 void acceptGoalInvitation_shouldThrowExceptionWhenUserNotFound() {
  long invitationId = 1L;

  User invitedUser = new User();
  invitedUser.setId(1L);

  GoalInvitation goalInvitation = new GoalInvitation();
  goalInvitation.setId(invitationId);
  goalInvitation.setInvited(invitedUser);

  when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.of(goalInvitation));
  when(userRepository.existsById(invitedUser.getId())).thenReturn(false);

  assertThrows(IllegalArgumentException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
 }

 @Test
 @DisplayName("Test accept goal invitation when goal invitation not found")
 void acceptGoalInvitation_shouldThrowExceptionWhenGoalInvitationNotFound() {
  long invitationId = 1L;
  User invitedUser = new User();
  invitedUser.setId(1L);

  when(goalInvitationRepository.findById(invitationId)).thenReturn(Optional.empty());

  assertThrows(NoSuchElementException.class, () -> goalInvitationService.acceptGoalInvitation(invitationId));
 }
}
