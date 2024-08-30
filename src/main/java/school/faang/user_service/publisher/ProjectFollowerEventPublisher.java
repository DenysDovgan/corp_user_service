package school.faang.user_service.publisher;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import school.faang.user_service.event.ProjectFollowerEvent;

@Service
public class ProjectFollowerEventPublisher extends AbstractEventPublisher<ProjectFollowerEvent> {
    public ProjectFollowerEventPublisher(RedisTemplate redisTemplate, @Qualifier("projectFollowerTopic") ChannelTopic topic) {
        super(redisTemplate, topic);
    }

    public void sendEvent(ProjectFollowerEvent projectFollowerEvent) {
        publish(projectFollowerEvent);
    }
}
