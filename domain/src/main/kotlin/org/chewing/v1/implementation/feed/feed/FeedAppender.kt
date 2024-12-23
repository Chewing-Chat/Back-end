package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FeedAppender(
    private val feedRepository: FeedRepository,
    private val feedDetailRepository: FeedDetailRepository,
) {
    @Transactional
    fun append(medias: List<Media>, userId: String, topic: String): String {
        val feedId = feedRepository.append(userId, topic)
        feedDetailRepository.append(medias, feedId)
        return feedId
    }
}
