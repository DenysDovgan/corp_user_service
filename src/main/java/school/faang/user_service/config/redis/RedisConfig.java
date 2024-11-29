package school.faang.user_service.config.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import school.faang.user_service.service.redis.RedisMessageSubscriber;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    @Value("${spring.data.redis.channels.user_ban_channel.name}")
    private String userBan;

    @Bean
    public RedisMessageListenerContainer redisContainer(RedisConnectionFactory connectionFactory, RedisMessageSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new PatternTopic(userBan));
        return container;
    }

    @Bean
    public StringRedisTemplate template(RedisConnectionFactory connectionFactory){
        return new StringRedisTemplate(connectionFactory);
    }

}
