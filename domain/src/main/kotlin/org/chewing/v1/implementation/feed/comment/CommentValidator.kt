package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.repository.feed.CommentRepository
import org.springframework.stereotype.Component

@Component
class CommentValidator(
    private val commentRepository: CommentRepository,
) {
    fun isOwned(userId: String, commentIds: List<String>) {
        val comments = commentRepository.readsIn(commentIds)
        if (comments.size != commentIds.size) {
            throw ConflictException(ErrorCode.FEED_COMMENT_NOT_FOUND)
        }
        if (comments.any { it.userId != userId }) {
            throw ConflictException(ErrorCode.FEED_COMMENT_IS_NOT_OWNED)
        }
    }
}
