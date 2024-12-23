package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.repository.feed.CommentRepository
import org.springframework.stereotype.Component

@Component
class CommentAppender(
    private val commentRepository: CommentRepository,
) {
    fun appendComment(userId: String, feedId: String, comment: String): String {
        return commentRepository.append(userId, feedId, comment)
    }
}
