package school.faang.user_service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class failTest {

    @Test
    public void createSkillWithEmptyTitleTest() {
        assertEquals(5, 2 + 2);
    }
}
