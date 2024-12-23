package org.chewing.v1.implementation.feed.like

import org.chewing.v1.repository.feed.FeedLikesRepository
import org.springframework.stereotype.Component

@Component
class FeedLikeAppender(
    private val feedLikesRepository: FeedLikesRepository,
) {
    fun appendLikes(feedId: String, userId: String) {
        feedLikesRepository.likes(feedId, userId)
    }
}
