package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.feed.FeedInfo
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
) : BaseEntity() {
    companion object {
        fun generate(
            content: String,
            userId: String,
        ): FeedJpaEntity {
            return FeedJpaEntity(
                feedContent = content,
                userId = userId,
            )
        }
    }

    fun toFeedId(): String {
        return feedId
    }
    fun toFeedInfo(): FeedInfo {
        return FeedInfo
            .of(
                feedId = feedId,
                content = feedContent,
                uploadAt = createdAt,
                userId = userId,
            )
    }
}
