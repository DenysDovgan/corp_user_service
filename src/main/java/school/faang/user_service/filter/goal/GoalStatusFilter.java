package school.faang.user_service.filter.goal;

import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.goal.Goal;

import java.util.Objects;
import java.util.stream.Stream;

public class GoalStatusFilter implements GoalFilter {

    @Override
    public boolean isApplicable(GoalFilterDto filter) {
        return filter != null && filter.getStatus() != null;
    }

    @Override
    public Stream<Goal> apply(Stream<Goal> goals, GoalFilterDto filter) {
        Objects.requireNonNull(goals, "Goal stream cannot be null");
        Objects.requireNonNull(filter, "Goal filter cannot be null");

        return goals.filter(goal -> goal.getStatus().equals(filter.getStatus()));
    }
}
