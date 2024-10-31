package school.faang.user_service.producer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import school.faang.user_service.model.event.GoalCompletedEvent;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GoalCompletedEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic channelTopic;

    @InjectMocks
    private GoalCompletedEventPublisher goalCompletedEventPublisher;

    @Test
    @DisplayName("Send Event Test")
    void  publish_isOk() {
        var goalCompletedEvent = GoalCompletedEvent.builder().build();
        goalCompletedEventPublisher.publish(goalCompletedEvent);
        verify(redisTemplate).convertAndSend(channelTopic.getTopic(), goalCompletedEvent);
    }
}