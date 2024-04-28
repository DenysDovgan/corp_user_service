package school.faang.user_service.service.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import school.faang.user_service.dto.event.FollowerEvent;


@Component

public class FollowerEventPublisher extends AbstractPublisher<FollowerEvent> {
    public FollowerEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper jsonMapper,
                                  @Value("${spring.data.redis.channels.follower_channel.name}") String followerChannelTopic) {

        super( redisTemplate, jsonMapper, followerChannelTopic );

    }
}



