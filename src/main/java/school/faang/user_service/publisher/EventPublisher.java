package school.faang.user_service.publisher;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

@Slf4j
public abstract class EventPublisher<T> {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    protected void convertAndSend(T eventDto, String topicName) {
        try {
            String json = objectMapper.writeValueAsString(eventDto);
            redisTemplate.convertAndSend(topicName, json);
            log.info("Event was send to topic : {}", topicName);
        } catch (JsonProcessingException e) {
            log.error("Failed attempt to convert to json");
            throw new RuntimeException(e.getMessage());
        }
    }
}
