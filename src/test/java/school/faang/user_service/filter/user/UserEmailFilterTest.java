package school.faang.user_service.filter.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import school.faang.user_service.dto.UserFilterDto;
import school.faang.user_service.entity.User;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserEmailFilterTest {

    private UserEmailFilter userEmailFilter;
    private UserFilterDto filter;

    @BeforeEach
    void setUp() {
        filter = new UserFilterDto();
        userEmailFilter = new UserEmailFilter();
    }

    @Test
    public void isApplicableFilterTest() {
        filter.setEmailPattern("Paris");

        assertTrue(userEmailFilter.isApplicable(filter));
    }

    @Test
    public void filterIsNullTest() {
        UserFilterDto emptyFilter = null;
        assertFalse(userEmailFilter.isApplicable(emptyFilter));
    }

    @Test
    public void emailPatternIsNullTest() {
        assertFalse(userEmailFilter.isApplicable(filter));
    }

    @Test
    public void emailPatternIsBlank() {
        filter.setEmailPattern("");

        assertFalse(userEmailFilter.isApplicable(filter));
    }

    @Test
    public void applyWithMatchingEmailPatternShouldReturnFilteredUsersTest() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("email");
        User user2 = mock(User.class);
        when(user2.getEmail()).thenReturn(".com");
        filter.setEmailPattern("email");
        Stream<User> users = Stream.of(user, user2);

        List<User> filteredUsers = userEmailFilter.apply(users, filter).toList();

        assertEquals(1, filteredUsers.size());
        assertTrue(filteredUsers.contains(user));
        assertFalse(filteredUsers.contains(user2));
    }

    @Test
    public void applyWithNonMatchingEmailPatternShouldReturnEmptyStreamTest() {
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("email");
        filter.setEmailPattern("another");
        Stream<User> users = Stream.of(user);

        List<User> filteredUsers = userEmailFilter.apply(users, filter).toList();

        assertTrue(filteredUsers.isEmpty());
    }
}
