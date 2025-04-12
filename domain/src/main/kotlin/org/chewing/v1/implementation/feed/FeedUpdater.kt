package org.chewing.v1.implementation.feed

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Component

@Component
class FeedUpdater(
    private val feedRepository: FeedRepository,
) {
    fun update(feedId: FeedId, content: String) {
        feedRepository.update(feedId, content)
    }
}
