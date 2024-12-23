package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Component

@Component
class FeedRemover(
    val feedRepository: FeedRepository,
    val feedDetailRepository: FeedDetailRepository,
) {
    fun removes(feedIds: List<String>): List<Media> {
        feedRepository.removes(feedIds)
        return feedDetailRepository.removes(feedIds)
    }
}
