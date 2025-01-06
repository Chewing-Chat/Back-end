package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.springframework.stereotype.Component

@Component
class FeedValidator(
    private val feedRepository: FeedRepository,
    private val feedVisibilityRepository: FeedVisibilityRepository,
) {
    fun isFeedsOwner(feedIds: List<String>, userId: String) {
        if (!feedRepository.isOwners(feedIds, userId)) {
            throw ConflictException(ErrorCode.FEED_IS_NOT_OWNED)
        }
    }

    fun isFeedVisible(feedId: String, userId: String) {
        if (!feedVisibilityRepository.isVisible(feedId, userId)) {
            throw ConflictException(ErrorCode.FEED_IS_NOT_VISIBLE)
        }
    }
}
