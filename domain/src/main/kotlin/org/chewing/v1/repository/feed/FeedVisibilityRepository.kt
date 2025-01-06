package org.chewing.v1.repository.feed

import org.springframework.stereotype.Repository

@Repository
interface FeedVisibilityRepository {
    fun append(feedId: String, targetUserIds: List<String>)
    fun isVisible(feedId: String, userId: String): Boolean
    fun readVisibleFeedIds(userId: String, feedIds: List<String>): List<String>
    fun removes(feedIds: List<String>)
}
