package school.faang.user_service.redis.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecommendationPublisher implements MessagePublisher{

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic recommendationChannel;

    @Override
    public void publishMessage(String message) {
        redisTemplate.convertAndSend(recommendationChannel.getTopic(), message);
    }
}
