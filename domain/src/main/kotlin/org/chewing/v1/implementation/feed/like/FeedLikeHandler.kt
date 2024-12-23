package org.chewing.v1.implementation.feed.like

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.model.feed.FeedTarget
import org.springframework.stereotype.Component

@Component
class FeedLikeHandler(
    private val feedLikeProcessor: FeedLikeProcessor,
    private val optimisticLockHandler: OptimisticLockHandler,
) {

    fun handleFeedLikes(feedId: String, userId: String, updateType: FeedTarget) {
        optimisticLockHandler.retryOnOptimisticLock {
            feedLikeProcessor.processFeedLikes(feedId, userId, updateType)
        }.let { result ->
            if (result == null) {
                throw ConflictException(ErrorCode.FEED_LIKED_FAILED)
            }
        }
    }

    fun handleFeedUnLikes(feedId: String, userId: String, updateType: FeedTarget) {
        optimisticLockHandler.retryOnOptimisticLock {
            feedLikeProcessor.processFeedUnLikes(feedId, userId, updateType)
        }.let {
            if (it == null) {
                throw ConflictException(ErrorCode.FEED_UNLIKED_FAILED)
            }
        }
    }
}
