package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Component

@Component
class FeedValidator(
    private val feedRepository: FeedRepository,
) {
    fun isFeedsOwner(feedIds: List<String>, userId: String) {
        val feedInfos = feedRepository.reads(feedIds)
        if (feedInfos.any { it.userId != userId }) {
            throw ConflictException(ErrorCode.FEED_IS_NOT_OWNED)
        }
    }
}
