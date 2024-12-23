package org.chewing.v1.implementation.feed.like

import org.chewing.v1.repository.feed.FeedLikesRepository
import org.springframework.stereotype.Component

@Component
class FeedLikeRemover(
    private val feedLikesRepository: FeedLikesRepository,
) {
    fun removeLikes(feedId: String, userId: String) {
        feedLikesRepository.unlikes(feedId, userId)
    }
    fun removeAll(feedIds: List<String>) {
        feedLikesRepository.unlikeAll(feedIds)
    }
}
