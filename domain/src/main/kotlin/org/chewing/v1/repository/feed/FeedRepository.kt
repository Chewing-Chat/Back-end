package org.chewing.v1.repository.feed

import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.model.feed.FeedTarget
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository {
    fun read(feedId: String): FeedInfo?
    fun reads(feedIds: List<String>): List<FeedInfo>
    fun readsOwned(userId: String, feedStatus: FeedStatus): List<FeedInfo>
    fun readsFriendBetween(userId: String, dateTarget: DateTarget): List<FeedInfo>
    fun removes(feedIds: List<String>)
    fun removesOwned(userId: String)
    fun append(userId: String, topic: String): String
    fun update(feedId: String, target: FeedTarget): String?
}
