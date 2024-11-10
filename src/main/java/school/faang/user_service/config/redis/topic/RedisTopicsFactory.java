package school.faang.user_service.config.redis.topic;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.Topic;

@Configuration
public class RedisTopicsFactory {
    @Value("${spring.data.redis.channel-topics.event-start.name}")
    private String eventStartTopicName;

    @Value("${redis.banner.topic}")
    private String userBanTopic;

    @Value("${app.user-redis-config.profile_view_event_topic}")
    private String profileViewEventTopic;

    @Value("${app.premium-redis-config.premium_bought_event_topic}")
    private String premiumBoughtEvent;

    @Value("${spring.data.redis.channel-topics.mentorship.request_received}")
    private String mentorshipRequestReceivedTopicName;

    @Value("${spring.data.redis.channel-topics.mentorship.request_accepted}")
    private String mentorshipRequestAcceptedTopicName;

    @Bean
    public Topic eventStartTopic() {
        return new ChannelTopic(eventStartTopicName);
    }

    @Bean
    public ChannelTopic userBanTopic() {
        return new ChannelTopic(userBanTopic);
    }

    @Bean
    public ChannelTopic profileViewEventTopic() {
        return new ChannelTopic(profileViewEventTopic);
    }

    @Bean
    public ChannelTopic premiumBoughtEventTopic() {
        return new ChannelTopic(premiumBoughtEvent);
    }

    @Bean
    public Topic mentorshipRequestReceivedTopicName() {
        return new ChannelTopic(mentorshipRequestReceivedTopicName);
    }

    @Bean
    public Topic mentorshipRequestAcceptedTopicName() {
        return new ChannelTopic(mentorshipRequestAcceptedTopicName);
    }
}
