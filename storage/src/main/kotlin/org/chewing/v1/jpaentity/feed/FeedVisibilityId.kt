package org.chewing.v1.jpaentity.feed

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.user.UserId
import java.io.Serializable
@Embeddable
class FeedVisibilityId(
    @Column(name = "feed_id")
    val feedId: String,
    @Column(name = "user_id")
    val userId: String,
) : Serializable {
    companion object {
        fun of(feedId: FeedId, userId: UserId): FeedVisibilityId {
            return FeedVisibilityId(feedId.id, userId.id)
        }
    }
}
