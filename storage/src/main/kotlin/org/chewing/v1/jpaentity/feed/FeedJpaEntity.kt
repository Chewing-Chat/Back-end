package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.*

@DynamicInsert
@Entity
@Table(
    name = "feed",
    schema = "chewing",
)
internal class FeedJpaEntity(
    @Id
    private val feedId: String = UUID.randomUUID().toString(),
    private val feedContent: String,
    private val userId: String,
    @Enumerated(EnumType.STRING)
    private val feedType: FeedType
) : BaseEntity() {
    companion object {
        fun generate(
            content: String,
            userId: UserId,
            feedType: FeedType
        ): FeedJpaEntity {
            return FeedJpaEntity(
                feedContent = content,
                userId = userId.id,
                feedType = feedType
            )
        }
    }

    fun toFeedId(): FeedId {
        return FeedId.of(feedId)
    }
    fun toFeedInfo(): FeedInfo {
        return FeedInfo
            .of(
                feedId = FeedId.of(feedId),
                content = feedContent,
                uploadAt = createdAt,
                userId = UserId.of(userId),
                type = feedType
            )
    }
}
