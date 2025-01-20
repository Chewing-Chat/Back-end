package org.chewing.v1.repository.feed

import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FeedVisibilityRepository {
    fun append(feedId: String, targetUserIds: List<UserId>)
    fun isVisible(feedId: String, userId: UserId): Boolean
    fun readVisibleFeedIds(userId: UserId, feedIds: List<String>): List<String>
    fun removes(feedIds: List<String>)
}
