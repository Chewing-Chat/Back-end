package org.chewing.v1.model.comment

import org.chewing.v1.model.user.User
import java.time.LocalDateTime

class Comment(
    val id: String,
    val comment: String,
    val createAt: LocalDateTime,
    val writer: User,
) {
    companion object {
        fun of(
            commentId: String,
            comment: String,
            createAt: LocalDateTime,
            writer: User,
        ): Comment {
            return Comment(commentId, comment, createAt, writer)
        }
    }
}
