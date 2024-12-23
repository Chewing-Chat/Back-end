package org.chewing.v1.dto.response.comment

import org.chewing.v1.model.comment.Comment
import java.time.format.DateTimeFormatter

data class CommentResponse(
    val commentId: String,
    val comment: String,
    val commentTime: String,
) {
    companion object {
        fun of(
            comment: Comment,
        ): CommentResponse {
            val formattedCommentTime = comment.createAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
            return CommentResponse(comment.id, comment.comment, formattedCommentTime)
        }
    }
}
