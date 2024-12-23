package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.repository.feed.CommentRepository
import org.springframework.stereotype.Component

@Component
class CommentRemover(
    private val commentRepository: CommentRepository,
) {
    fun remove(commentId: String): String? = commentRepository.remove(commentId)

    fun removes(feedIds: List<String>) {
        commentRepository.removes(feedIds)
    }
}
