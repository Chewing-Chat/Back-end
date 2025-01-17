package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.media.Media
import org.chewing.v1.repository.feed.FeedDetailRepository
import org.chewing.v1.repository.feed.FeedRepository
import org.chewing.v1.repository.feed.FeedVisibilityRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FeedAppender(
    private val feedRepository: FeedRepository,
    private val feedDetailRepository: FeedDetailRepository,
    private val feedVisibilityRepository: FeedVisibilityRepository,
) {
    @Transactional
    fun append(medias: List<Media>, userId: String, content: String): String {
        val feedId = feedRepository.append(userId, content)
        feedDetailRepository.append(medias, feedId)
        return feedId
    }
    fun appendVisibility(feedId: String, targetUserIds: List<String>) {
        feedVisibilityRepository.append(feedId, targetUserIds)
    }
}
