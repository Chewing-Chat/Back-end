package org.chewing.v1.model.comment

import java.time.LocalDateTime

class CommentInfo private constructor(
    val commentId: String,
    val comment: String,
    val createAt: LocalDateTime,
    val userId: String,
    val feedId: String,
) {
    companion object {
        fun of(
            commentId: String,
            comment: String,
            createAt: LocalDateTime,
            userId: String,
            feedId: String,
        ): CommentInfo {
            return CommentInfo(commentId, comment, createAt, userId, feedId)
        }
    }
}
