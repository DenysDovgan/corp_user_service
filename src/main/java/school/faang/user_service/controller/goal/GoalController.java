package school.faang.user_service.controller.goal;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.service.goal.GoalService;

import java.util.List;

@Tag(name = "Goals", description = "API for managing user goals.")
@RestController
@RequestMapping("/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> createGoal(@PathVariable Long userId,
                                           @Valid @RequestBody GoalDto goalDto) {
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .path("/{goalId}")
                        .buildAndExpand(goalService.createGoal(userId, goalDto).getId())
                        .toUri()
        ).build();
    }

    @PutMapping("/{goalId}")
    public ResponseEntity<Void> updateGoal(@PathVariable("goalId") Long goalId,
                                           @Valid @RequestBody GoalDto goalDto) {
        goalService.updateGoal(goalId, goalDto);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{goalId}")
    public ResponseEntity<Void> deleteGoal(@PathVariable("goalId") Long goalId) {
        goalService.deleteGoal(goalId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<GoalDto>> getGoalsByUserId(@PathVariable("userId") Long userId,
                                                          @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.getGoalsByUserId(userId, filters));
    }

    @GetMapping("/{goalId}/subtasks")
    public ResponseEntity<List<GoalDto>> findSubtasksByGoalId(@PathVariable("goalId") Long goalId,
                                                              @RequestBody GoalFilterDto filters) {
        return ResponseEntity.ok(goalService.findSubtasksByGoalId(goalId, filters));
    }
}
