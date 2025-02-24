package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId
import org.hibernate.annotations.DynamicInsert
import java.util.*

@DynamicInsert
@Entity
@Table(
    name = "feed",
    schema = "chewing",
    indexes = [
        Index(name = "feed_idx_user_id_status", columnList = "userId, status"),
        Index(name = "feed_idx_user_id_created_at", columnList = "userId, created_at"),
        Index(name = "feed_idx_feed_id_user_id", columnList = "feedId, userId"),
    ],
)
internal class FeedJpaEntity(
    @Id
    private val feedId: String = UUID.randomUUID().toString(),
    private val content: String,
    private val userId: String,
    @Enumerated(EnumType.STRING)
    private val type: FeedType,
    @Enumerated(EnumType.STRING)
    private var status: FeedStatus,
) : BaseEntity() {
    companion object {
        fun generate(
            content: String,
            userId: UserId,
            feedType: FeedType,
        ): FeedJpaEntity {
            return FeedJpaEntity(
                content = content,
                userId = userId.id,
                type = feedType,
                status = FeedStatus.ACTIVE,
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
                content = content,
                uploadAt = createdAt,
                userId = UserId.of(userId),
                type = type,
            )
    }
    fun delete() {
        status = FeedStatus.DELETED
    }
}
