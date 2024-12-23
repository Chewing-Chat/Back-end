package org.chewing.v1.jpaentity.feed

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.comment.CommentInfo
import org.hibernate.annotations.DynamicInsert
import java.util.*

@Entity
@DynamicInsert
@Table(name = "feed_comment", schema = "chewing")
internal class FeedCommentJpaEntity(
    @Id
    private val feedCommentId: String = UUID.randomUUID().toString(),
    private val comment: String,
    private val userId: String,
    private val feedId: String,
) : BaseEntity() {
    companion object {
        fun generate(userId: String, feedId: String, comment: String): FeedCommentJpaEntity {
            return FeedCommentJpaEntity(
                comment = comment,
                userId = userId,
                feedId = feedId,
            )
        }
    }

    fun toCommentInfo(): CommentInfo {
        return CommentInfo.of(
            feedCommentId,
            comment,
            this.createdAt,
            userId,
            feedId,
        )
    }
}
