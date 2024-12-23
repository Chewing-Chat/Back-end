package org.chewing.v1.implementation.feed.comment

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class CommentHandler(
    private val commentProcessor: CommentProcessor,
    private val asyncJobExecutor: AsyncJobExecutor,
    private val optimisticLockHandler: OptimisticLockHandler,
) {
    fun handleComment(userId: String, feedId: String, comment: String, target: FeedTarget): String {
        val result = optimisticLockHandler.retryOnOptimisticLock {
            commentProcessor.processComment(userId, feedId, comment, target)
        }
        if (result == null) {
            throw ConflictException(ErrorCode.FEED_COMMENT_FAILED)
        }
        return result
    }

    fun handleUnComments(commentIds: List<String>, target: FeedTarget) {
        asyncJobExecutor.executeAsyncJobs(commentIds) { commentId ->
            optimisticLockHandler.retryOnOptimisticLock {
                commentProcessor.processUnComment(commentId, target)
            }
        }
    }
}
