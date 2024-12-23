package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.repository.feed.CommentRepository
import org.springframework.stereotype.Component

@Component
class CommentReader(
    private val commentRepository: CommentRepository,
) {
    fun reads(feedId: String): List<CommentInfo> {
        return commentRepository.reads(feedId)
    }

    fun readsOwned(userId: String): List<CommentInfo> {
        return commentRepository.readsOwned(userId)
    }
}
