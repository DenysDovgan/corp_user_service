package school.faang.user_service.service.user.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import school.faang.user_service.dto.filter.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static school.faang.user_service.service.user.filter.TestData.ALL_USERS;

class UserCityFilterTest {
    private final UserCityFilter userCityFilter = new UserCityFilter();
    private UserFilterDto filter;
    private List<User> usersToFilter;
    private Stream<User> expectedFilteredUsers;

    @BeforeEach
    void setUp() {
        usersToFilter = ALL_USERS;

        filter = new UserFilterDto();
        filter.setCityPattern("Lon.*");

        expectedFilteredUsers = Stream.of(ALL_USERS.get(1));
    }

    @Nested
    class PositiveTests {
        @DisplayName("should return true when \"cityPattern\" is present")
        @Test
        void shouldReturnTrueWhenCityPatternIsPresent() {
            var isApplicable = userCityFilter.isApplicable(filter);

            assertTrue(isApplicable);
        }

        @DisplayName("should return filtered by \"cityPattern\" users ")
        @Test
        void shouldReturnFilteredUsersWhenCityFilterIsPresent() {
            var actualFilteredUsers = userCityFilter.apply(usersToFilter, filter);

            assertEquals(expectedFilteredUsers.toList(), actualFilteredUsers.toList());
        }
    }

    @Nested
    class NegativeTests {
        @DisplayName("should return false when \"cityPattern\" is empty")
        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"  ", "\t", "\n"})
        void shouldReturnFalseWhenCityPatternIsEmpty(String pattern) {
            filter.setCityPattern(pattern);

            var isApplicable = userCityFilter.isApplicable(filter);

            assertFalse(isApplicable);
        }

        @DisplayName("should return empty list when no one user is matching filter")
        @Test
        void shouldReturnEmptyListWhenNothingMatching() {
            filter.setCityPattern("Cahul");

            var actualFilteredUsers = userCityFilter.apply(usersToFilter, filter);

            assertEquals(List.of(), actualFilteredUsers.toList());
        }
    }
}