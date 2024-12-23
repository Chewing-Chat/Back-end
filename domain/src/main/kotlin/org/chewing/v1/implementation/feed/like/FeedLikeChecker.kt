package org.chewing.v1.implementation.feed.like

import org.chewing.v1.repository.feed.FeedLikesRepository
import org.springframework.stereotype.Component

@Component
class FeedLikeChecker(
    private val feedLikesRepository: FeedLikesRepository,
) {
    fun checkLike(feedId: String, userId: String): Boolean {
        return feedLikesRepository.checkLike(feedId, userId)
    }
}
