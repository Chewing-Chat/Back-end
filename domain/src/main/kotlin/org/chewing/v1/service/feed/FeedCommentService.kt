package org.chewing.v1.service.feed

import org.chewing.v1.implementation.feed.comment.*
import org.chewing.v1.model.comment.CommentInfo
import org.chewing.v1.model.feed.FeedTarget
import org.springframework.stereotype.Service

@Service
class FeedCommentService(
    private val commentReader: CommentReader,
    private val commentHandler: CommentHandler,
    private val commentRemover: CommentRemover,
    private val commentValidator: CommentValidator,
) {
    fun remove(userId: String, commentIds: List<String>, target: FeedTarget) {
        commentValidator.isOwned(userId, commentIds)
        commentHandler.handleUnComments(commentIds, target)
    }

    fun removes(feedIds: List<String>) {
        commentRemover.removes(feedIds)
    }

    fun comment(userId: String, feedId: String, comment: String, target: FeedTarget): String {
        return commentHandler.handleComment(userId, feedId, comment, target)
    }

    fun getOwnedComment(userId: String): List<CommentInfo> = commentReader.readsOwned(userId)

    fun getComment(feedId: String): List<CommentInfo> = commentReader.reads(feedId)
}
