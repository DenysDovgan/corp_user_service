package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.mapper.goal.GoalMapper;
import school.faang.user_service.repository.SkillRepository;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.user.UserService;
import school.faang.user_service.validator.goal.GoalValidator;
import school.faang.user_service.validator.skill.SkillValidator;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;
    private final List<GoalFilter> goalFilters;
    private final GoalValidator goalValidator;
    private final SkillRepository skillRepository;
    private final UserService userService;
    private final SkillValidator skillValidator;
    private final UserRepository userRepository;

    @Transactional
    public GoalDto createGoal(Long userId, GoalDto goalDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + userId));
        Goal goalToSave = goalMapper.toGoal(goalDto);
        if (goalDto.getParentId() != null) {
            Goal parentGoal = goalRepository.findById(goalDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalDto.getParentId()));
            goalToSave.setParent(parentGoal);
        }
        //todo: Добавить проверку скилл айди
        if (goalDto.getSkillIds() != null) {
            Goal parentGoal = goalRepository.findById(goalDto.getParentId())
                    .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalDto.getParentId()));
            goalToSave.setParent(parentGoal);
        }
        if (goalDto.getMentorId() != null) {
            User mentor = userRepository.findById(goalDto.getMentorId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found by Id: " + goalDto.getMentorId()));
            if (!user.getMentors().contains(mentor)) {
            //todo: Добавить кастомную ошибку
                throw new EntityNotFoundException();
            }
            goalToSave.setMentor(mentor);
        }
        if (goalDto.getDeadline() != null) {
            goalToSave.setDeadline(goalDto.getDeadline());
        }
        goalToSave.setStatus(GoalStatus.ACTIVE);
        Goal savedGoal = goalRepository.save(goalToSave);
        return goalMapper.toGoalDto(savedGoal);
    }

    @Transactional
    public GoalDto updateGoal(long goalId, GoalDto goalDto) {

        Goal goalToUpdate = goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal not found by Id: " + goalId));
        if(goalDto.getParentId() != null) {}



        Goal existingGoal = getGoalById(goalId);

        Goal updatedGoal = updateGoalEntity(existingGoal, goalDto);

        return goalMapper.toGoalDto(updatedGoal);
    }

    @Transactional
    public void deleteGoal(long goalId) {
        goalRepository.deleteById(goalId);
    }

    @Transactional
    public List<GoalDto> findSubtasksByGoalId(long goalId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.findByParent(goalId);

        return filterGoals(goals, filterDto);
    }

    @Transactional
    public List<GoalDto> findGoalsByUserId(long userId, GoalFilterDto filterDto) {
        Stream<Goal> goals = goalRepository.getGoalsByUserIdId(userId);

        return filterGoals(goals, filterDto);
    }

    private Goal getGoalById(long goalId) {
        return goalRepository.findById(goalId)
                .orElseThrow(() -> new EntityNotFoundException("Goal with this id does not exist in the database"));
    }

    private Goal createGoalEntity(GoalDto goalDto, User user) {
        Goal goal = goalRepository.create(goalDto.getTitle(), goalDto.getDescription(), goalDto.getParentId());

        goal.getUsers().add(user);
        setSkillsToGoal(goalDto.getSkillIds(), goal);

        goalRepository.save(goal);
        return goal;
    }

    private Goal updateGoalEntity(Goal existingGoal, GoalDto goalDto) {
        goalValidator.validateGoalStatusNotCompleted(existingGoal);

        existingGoal.setTitle(goalDto.getTitle());
        existingGoal.setDescription(goalDto.getDescription());

        GoalStatus status = goalDto.getStatus();
        if (status != null) {
            existingGoal.setStatus(status);
        }

        Long parentId = goalDto.getParentId();
        if (parentId != null) {
            Goal parentGoal = getGoalById(parentId);
            existingGoal.setParent(parentGoal);
        }

        setSkillsToGoal(goalDto.getSkillIds(), existingGoal);

        goalRepository.save(existingGoal);

        return existingGoal;
    }

    private void setSkillsToGoal(List<Long> skillIds, Goal goal) {
        if (skillIds != null) {
            skillValidator.validateAllSkillsIdsExist(skillIds);
            List<Skill> skills = skillRepository.findAllById(skillIds);
            goal.setSkillsToAchieve(skills);
        }
    }

    private List<GoalDto> filterGoals(Stream<Goal> goals, GoalFilterDto filterDto) {
        return goalFilters.stream()
                .filter(filter -> filter.isApplicable(filterDto))
                .reduce(goals, (currentGoals, filter) -> filter.apply(currentGoals, filterDto), (s1, s2) -> s1)
                .map(goalMapper::toGoalDto)
                .toList();
    }

}
