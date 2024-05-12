package school.faang.user_service.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import school.faang.user_service.subscriber.UsersBanListener;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final ObjectMapper objectMapper;
    @Value("${spring.data.redis.host}")
    private String host;
    @Value("${spring.data.redis.port}")
    private int port;

    private final UsersBanListener usersBanListener;

    @Value("${spring.data.redis.channels.search_appearance_channel.name}")
    private String searchAppearanceTopic;

    @Value("${spring.data.redis.channels.recommendation_request_channel.name}")
    private String recommendationRequestChannel;
    @Value("${spring.data.redis.channels.recommendation_channel.name}")
    private String recommendationChannel;

    @Value("${topic.user_ban}")
    private String userBanTopic;

    @Value("${spring.data.redis.channels.goal_set_channel.name}")
    private String goalSetChannel;

    @Value("${spring.data.redis.channels.profile_view_channel.name}")
    private String profileViewChannel;

    @Value("${spring.data.redis.channels.skill_channel.name}")
    private String skillChannel;

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanTopic);
    }


    @Bean
    public MessageListenerAdapter userBanMessageListenerAdapter() {
        return new MessageListenerAdapter(usersBanListener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(userBanMessageListenerAdapter(), userBanTopic());
        return container;
    }


    @Bean
    public ChannelTopic recommendationRequestTopic() {
        return new ChannelTopic(recommendationRequestChannel);
    }
    @Bean
    public ChannelTopic SearchAppearanceTopic() {
        return new ChannelTopic(searchAppearanceTopic);
    }


    @Bean
    public ChannelTopic recommendationTopic(){
        return new ChannelTopic(recommendationChannel);
    }

    @Bean
    public ChannelTopic skillTopic() {
        return new ChannelTopic(skillChannel);
    }

    @Bean
    public ChannelTopic goalSetTopic() {
        return new ChannelTopic(goalSetChannel);
    }

    @Bean
    public ChannelTopic profileViewTopic() {
        return new ChannelTopic(profileViewChannel);
    }
}