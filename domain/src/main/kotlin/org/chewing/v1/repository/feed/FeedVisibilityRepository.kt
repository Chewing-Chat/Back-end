package org.chewing.v1.repository.feed

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FeedVisibilityRepository {
    fun append(feedId: FeedId, targetUserIds: List<UserId>)
    fun isVisible(feedId: FeedId, userId: UserId): Boolean
    fun readVisibleFeedIds(userId: UserId, feedIds: List<FeedId>): List<FeedId>
    fun removes(feedIds: List<FeedId>)
}
