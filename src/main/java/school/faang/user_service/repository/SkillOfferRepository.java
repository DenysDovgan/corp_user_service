package school.faang.user_service.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import school.faang.user_service.entity.recommendation.SkillOffer;

@Repository
public interface SkillOfferRepository extends CrudRepository<SkillOffer, Long> {

    @Query(nativeQuery = true, value = "INSERT INTO skill_offer (skill_id, recommendation_id) VALUES (:skillId, :recommendationId)")
    @Modifying
    SkillOffer create(long skillId, long recommendationId);

    void deleteAllByRecommendationId(long recommendationId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(so.id) FROM skill_offer so
            JOIN recommendation r ON r.id = so.recommendation_id AND r.receiver_id = :userId
            WHERE so.skill_id = :skillId
            """)
    int countAllOffersOfSkill(long skillId, long userId);
}
