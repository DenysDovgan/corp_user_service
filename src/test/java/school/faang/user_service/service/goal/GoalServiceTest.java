package school.faang.user_service.service.goal;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import school.faang.user_service.dto.goal.GoalDto;
import school.faang.user_service.dto.goal.GoalFilterDto;
import school.faang.user_service.entity.Skill;
import school.faang.user_service.entity.User;
import school.faang.user_service.entity.goal.Goal;
import school.faang.user_service.entity.goal.GoalStatus;
import school.faang.user_service.exceptions.GoalOverflowException;
import school.faang.user_service.filter.goal.GoalFilter;
import school.faang.user_service.filter.goal.GoalStatusFilter;
import school.faang.user_service.filter.goal.GoalTitleFilter;
import school.faang.user_service.mapper.GoalMapperImpl;
import school.faang.user_service.repository.UserRepository;
import school.faang.user_service.repository.goal.GoalRepository;
import school.faang.user_service.service.skill.SkillService;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private GoalRepository goalRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SkillService skillService;
    @Spy
    private GoalMapperImpl goalMapper;
    @Mock
    private List<GoalFilter> filters;


    @InjectMocks
    private GoalService goalService;

    Goal goal;
    Goal goal2;
    Goal goal3;
    Goal goal4;
    Goal goal5;
    Goal goal6;
    User user;
    User user2;
    User user3;
    User user4;
    User user5;
    User user6;
    GoalDto goalDto1;
    GoalDto goalDto2;
    GoalDto goalDto3;
    GoalDto goalDto4;
    GoalDto goalDto5;
    GoalDto goalDto6;
    GoalFilterDto goalFilterDto;
    Stream<Goal> goals;
    Stream<Goal> goals1;
    Stream<Goal> goals2;
    Stream<GoalFilter> filterStream;

    @BeforeEach
    void init() {

        Skill skill1 = Skill.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        Skill skill2 = Skill.builder()
                .id(2L)
                .build();

        Skill skill3 = Skill.builder()
                .id(3L)
                .build();

        goal = Goal.builder()
                .id(1L)
                .title("title")
                .description("descriptional")
                .skillsToAchieve(Arrays.asList(skill1, skill2, skill3))
                .build();

        goal2 = Goal.builder()
                .id(2L)
                .skillsToAchieve(Arrays.asList(skill1, skill2))
                .build();

        goal3 = Goal.builder()
                .id(1L)
                .title("t")
                .description("d")
                .parent(goal2)
                .skillsToAchieve(Collections.singletonList(skill1))
                .build();

        goal4 = Goal.builder()
                .id(4L)
                .status(GoalStatus.ACTIVE)
                .title("Java")
                .build();

        goal5 = Goal.builder()
                .id(5L)
                .status(GoalStatus.ACTIVE)
                .title("python")
                .build();

        goal6 = Goal.builder()
                .id(6L)
                .parent(goal4)
                .title("Java")
                .build();

        goalDto1 = GoalDto.builder()
                .id(1L)
                .title("title")
                .description("descriptional")
                .parentId(3L)
                .skillIds(Arrays.asList(1L, 2L, 3L))
                .build();

        goalDto2 = GoalDto.builder()
                .id(2L)
                .skillIds(Arrays.asList(1L, 2L))
                .build();

        goalDto3 = GoalDto.builder()
                .id(3L)
                .title("t")
                .description("d")
                .parentId(1L)
                .build();

        goalDto4 = GoalDto.builder()
                .id(4L)
                .skillIds(Collections.emptyList())
                .status(GoalStatus.ACTIVE)
                .title("Java")
                .build();

        goalDto5 = GoalDto.builder()
                .id(5L)
                .status(GoalStatus.ACTIVE)
                .title("python")
                .build();

        goalDto6 = GoalDto.builder()
                .id(6L)
                .title("Java")
                .build();

        user = User.builder()
                .id(1L)
                .goals(new ArrayList<>())
                .build();

        user2 = User.builder()
                .id(1L)
                .goals(Collections.singletonList(goal2))
                .build();

        user3 = User.builder()
                .id(1L)
                .goals(Arrays.asList(goal, goal2, goal3))
                .build();

        user4 = User.builder()
                .id(4L)
                .goals(Arrays.asList(goal4, goal5))
                .build();

        user5 = User.builder()
                .id(5L)
                .build();

        user6 = User.builder()
                .id(1L)
                .goals(Collections.singletonList(goal2))
                .build();

        goalFilterDto = GoalFilterDto.builder()
                .title("Java")
                .build();

        filterStream = Stream.of(new GoalStatusFilter(), new GoalTitleFilter());

        goals = Stream.of(goal4, goal5);
    }

    @Test
    @DisplayName("Missing target remove test")
    void testDeleteGoalById() {
        goalService.deleteGoal(goal.getId());
        verify(goalRepository).deleteById(goal.getId());
    }

    @Test
    @DisplayName("Test create goal and ")
    void testCreateGoal() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    @DisplayName("Test throwing an exception Goal Overflow Exception")
    public void shouldGoalOverflowException() {
        when(userRepository.findById(user3.getId())).thenReturn(Optional.of(user3));
        assertThrows(GoalOverflowException.class, () -> goalService.createGoal(1L, goal));
    }

    @Test
    @DisplayName("Test save skill and goal")
    public void shouldSaveIsActiveSkillAndSaveGoal() {
        when(skillService.checkActiveSkill(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        goalService.createGoal(user.getId(), goal3);
        skillService.saveAll(goal2.getSkillsToAchieve());

        verify(goalRepository).create("t", "d", goal2.getId());
        verify(skillService).saveAll(goal3.getSkillsToAchieve());
    }

    @Test
    @DisplayName("Test for obtaining a list of goals from a user using a filter")
    public void shouldGetListOfGoalsByUserFromFilters() {
        goalFilterDto = GoalFilterDto.builder()
                .title("Java")
                .build();

        when(filters.stream()).thenReturn(filterStream);
        when(goalRepository.findGoalsByUserId(user4.getId())).thenReturn(goals);

        List<GoalDto> actualGoals = goalService.getGoalsByUser(user4.getId(), goalFilterDto);
        List<GoalDto> expectedGoals = Collections.singletonList(goalDto4);

        assertEquals(expectedGoals, actualGoals);
    }

    @Test
    @DisplayName("Test returning an empty goal list when no applicable filters are found")
    public void shouldReturnEmptyListOfGoalsWhenNoApplicableFiltersFound() {
        List<GoalDto> actualGoals = goalService.getGoalsByUser(user4.getId(), goalFilterDto);
        assertTrue(actualGoals.isEmpty());
    }

    @Test
    @DisplayName("Test return target subtasks by user id")
    void shouldReturnSubtasksByGoalId() {
        goals1 = Stream.of(goal2);
        when(goalRepository.findByParent(user2.getId())).thenReturn(goals1);
        List<GoalDto> actualGoals = goalService.findSubtasksByGoalId(user2.getId());
        List<GoalDto> expectedGoals = Collections.singletonList(goalDto2);

        assertEquals(expectedGoals, actualGoals);
    }

    @Test
    @DisplayName("Test for returning an empty list when there are no subtask")
    public void shouldReturningEmptyListSubtasksByGoalId() {
        List<GoalDto> actualGoals = goalService.findSubtasksByGoalId(user5.getId());
        assertTrue(actualGoals.isEmpty());
    }

    @Test
    @DisplayName("test return target subtasks by user id fnd filters ")
    public void shouldReturnSubtasksByGoalIdAndFilters() {
        goals2 = Stream.of(goal4);

        when(filters.stream()).thenReturn(filterStream);
        when(goalRepository.findByParent(user6.getId())).thenReturn(goals2);

        List<GoalDto> actualGoals = goalService.findSubtasksByGoalId(user6.getId(),goalFilterDto);
        List<GoalDto> expectedGoals = Collections.singletonList(goalDto4);

        assertEquals(expectedGoals, actualGoals);
    }

    @Test
    @DisplayName("Test return an empty sheet if there are no matches in the filters")
    public void shouldReturningEmptyListSubtasksWhenTheFiltersNotWork() {

        List<GoalDto> actualGoals = goalService.findSubtasksByGoalId(user5.getId(),goalFilterDto);
        assertTrue(actualGoals.isEmpty());
    }
}
