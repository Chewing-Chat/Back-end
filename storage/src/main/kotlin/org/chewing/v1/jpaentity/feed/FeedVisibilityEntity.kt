package org.chewing.v1.jpaentity.feed

import jakarta.persistence.EmbeddedId
import jakarta.persistence.Entity
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
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
        fun generate(feedId: String, userId: String): FeedVisibilityEntity {
            return FeedVisibilityEntity(
                id = FeedVisibilityId(feedId, userId),
            )
        }
    }
    fun getFeedId(): String {
        return id.feedId
    }
}
