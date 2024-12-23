package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.util.OptimisticLockHandler
import org.chewing.v1.model.feed.FeedTarget
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class FeedHandler(
    private val feedUpdater: FeedUpdater,
    private val asyncJobExecutor: AsyncJobExecutor,
    private val optimisticLockHandler: OptimisticLockHandler,

) {
    fun lockFeedHides(feedIds: List<String>, updateType: FeedTarget) {
        asyncJobExecutor.executeAsyncJobs(feedIds) { feedId ->
            optimisticLockHandler.retryOnOptimisticLock {
                feedUpdater.update(feedId, updateType)
            }
        }
    }
}
