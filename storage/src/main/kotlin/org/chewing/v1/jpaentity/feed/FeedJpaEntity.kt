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
    indexes = [Index(name = "idx_feed_hide", columnList = "hide")],
)
internal class FeedJpaEntity(
    @Id
    private val feedId: String = UUID.randomUUID().toString(),
    private val feedTopic: String,
    private var likes: Int,
    private var comments: Int,
    @Version
    private var version: Long? = 0,
    private val userId: String,
    private var hide: Boolean,
) : BaseEntity() {
    companion object {
        fun generate(
            topic: String,
            userId: String,
        ): FeedJpaEntity {
            return FeedJpaEntity(
                feedTopic = topic,
                likes = 0,
                comments = 0,
                userId = userId,
                hide = false,
            )
        }
    }

    fun likes() {
        this.likes += 1
    }

    fun unLikes() {
        this.likes -= 1
    }

    fun comments() {
        this.comments += 1
    }

    fun unComments() {
        this.comments -= 1
    }

    fun hide() {
        this.hide = true
    }

    fun unHide() {
        this.hide = false
    }

    fun toFeedId(): String {
        return feedId
    }
    fun toFeedInfo(): FeedInfo {
        return FeedInfo
            .of(
                feedId = feedId,
                topic = feedTopic,
                likes = likes,
                comments = comments,
                uploadAt = createdAt,
                userId = userId,
            )
    }
}
