package org.chewing.v1.implementation.feed.like

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.repository.feed.FeedLikesRepository
import org.springframework.stereotype.Component

@Component
class FeedLikeValidator(
    private val feedLikesRepository: FeedLikesRepository,
) {
    fun isAlreadyLiked(feedId: String, userId: String) {
        if (feedLikesRepository.checkLike(feedId, userId)) {
            throw ConflictException(ErrorCode.FEED_ALREADY_LIKED)
        }
    }

    fun isAlreadyUnliked(feedId: String, userId: String) {
        if (!feedLikesRepository.checkLike(feedId, userId)) {
            throw ConflictException(ErrorCode.FEED_ALREADY_UNLIKED)
        }
    }
}
