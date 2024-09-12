package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;
import school.faang.user_service.service.user.UserFilter;

import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserNameFilterTest {

    private UserFilter userNameFilter;
    private String nameTest;

    @BeforeEach
    void setUp() {
        userNameFilter = new UserNameFilter();
        nameTest = "Vasia";
    }

    @Test
    void testIsApplicable_patternWithFilledName() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern(nameTest);
        boolean isApplicable = userNameFilter.isApplicable(userFilterDto);

        assertTrue(isApplicable);
    }

    @Test
    void testIsApplicable_patternWithNotFilledName() {
        UserFilterDto userFilterDto = new UserFilterDto();
        boolean isApplicable = userNameFilter.isApplicable(userFilterDto);

        assertFalse(isApplicable);
    }

    @Test
    void testGetPredicate_successValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern(nameTest);

        User user = new User();
        user.setUsername(nameTest);

        Predicate<User> predicate =  userNameFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertTrue(result);
    }

    @Test
    void testGetPredicate_failedValidation() {
        UserFilterDto userFilterDto = new UserFilterDto();
        userFilterDto.setNamePattern(nameTest);

        User user = new User();
        user.setUsername("Kolia");

        Predicate<User> predicate =  userNameFilter.getPredicate(userFilterDto);

        boolean result = predicate.test(user);

        assertFalse(result);
    }


}
