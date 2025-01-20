package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
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
    fun append(medias: List<Media>, userId: UserId, content: String): FeedId {
        val feedId = feedRepository.append(userId, content)
        feedDetailRepository.append(medias, feedId)
        return feedId
    }
    fun appendVisibility(feedId: FeedId, targetUserIds: List<UserId>) {
        feedVisibilityRepository.append(feedId, targetUserIds)
    }
}
