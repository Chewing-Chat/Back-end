package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.hibernate.annotations.DynamicInsert
import java.time.LocalDateTime

@Entity
@DynamicInsert
@Table(name = "feed_likes", schema = "chewing")
internal class UserFeedLikesJpaEntity(
    @EmbeddedId
    private val feedLikeId: FeedLikeId,
    private val likeTime: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun fromUserFeed(userId: String, feedId: String): UserFeedLikesJpaEntity {
            return UserFeedLikesJpaEntity(
                feedLikeId = FeedLikeId(userId = userId, feedId = feedId),
            )
        }
    }
}
