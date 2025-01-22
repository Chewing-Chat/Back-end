package org.chewing.v1.implementation.feed

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.springframework.stereotype.Component

@Component
class FeedRemover(
    val feedRepository: FeedRepository,
    val feedDetailRepository: FeedDetailRepository,
    val feedVisibilityRepository: FeedVisibilityRepository,
) {
    fun removes(feedIds: List<FeedId>): List<Media> {
        feedRepository.removes(feedIds)
        feedVisibilityRepository.removes(feedIds)
        return feedDetailRepository.removes(feedIds)
    }
}
