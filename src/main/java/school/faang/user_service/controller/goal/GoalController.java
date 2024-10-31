package school.faang.user_service.controller.goal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@RestController
@RequestMapping("/v1/goals")
@AllArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GoalDto createGoal(@PathVariable("userId") @Positive Long userId,
                              @RequestBody @Valid GoalDto goalDto) {
        return goalService.createGoal(userId, goalDto);
    }

    @PatchMapping("/{goalId}")
    @ResponseStatus(HttpStatus.OK)
    public GoalDto updateGoal(@PathVariable("goalId") @Positive Long goalId,
                              @RequestBody @Valid GoalDto goal) {
        return goalService.updateGoal(goalId, goal);
    }

    @DeleteMapping("/{goalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteGoal(@PathVariable("goalId") @Positive Long goalId) {
        goalService.deleteGoal(goalId);
    }

    @GetMapping("/{goalId}/subtasks")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> findSubtasksByGoalId(@PathVariable("goalId") @Positive Long goalId,
                                              @RequestParam(value = "filterTitle", required = false) String filterTitle,
                                              @RequestParam(value = "filterDescription", required = false) String filterDescription,
                                              @RequestParam(value = "filterStatus", required = false) GoalStatus filterStatus,
                                              @RequestParam(value = "filterSkills", required = false) List<Long> filterSkills) {
        GoalFilterDto filterDto = new GoalFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return goalService.findSubtasksByGoalId(goalId, filterDto);
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<GoalDto> getGoalsByUser(@PathVariable("userId") @Positive Long userId,
                                        @RequestParam(value = "filterTitle", required = false) String filterTitle,
                                        @RequestParam(value = "filterDescription", required = false) String filterDescription,
                                        @RequestParam(value = "filterStatus", required = false) GoalStatus filterStatus,
                                        @RequestParam(value = "filterSkills", required = false) List<Long> filterSkills) {
        GoalFilterDto filterDto = new GoalFilterDto(filterTitle, filterDescription, filterStatus, filterSkills);

        return goalService.getGoalsByUser(userId, filterDto);
    }
}
