package org.chewing.v1.jpaentity.feed

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert

@DynamicInsert
@Entity
@Table(
    name = "feed_visibility",
    schema = "chewing",
)
internal class FeedVisibilityEntity(
    @EmbeddedId
    val id: FeedVisibilityId,
) : BaseEntity() {
    companion object {
        fun generate(feedId: FeedId, userId: UserId): FeedVisibilityEntity {
            return FeedVisibilityEntity(
                id = FeedVisibilityId.of(feedId, userId),
            )
        }
    }
    fun getFeedId(): FeedId {
        return FeedId.of(id.feedId)
    }
}
