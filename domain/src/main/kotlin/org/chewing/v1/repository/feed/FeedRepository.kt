package org.chewing.v1.repository.feed

import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedInfo
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository {
    fun read(feedId: String): FeedInfo?
    fun reads(userId: String): List<FeedInfo>
    fun readsFriendBetween(userId: String, dateTarget: DateTarget): List<FeedInfo>
    fun removes(feedIds: List<String>)
    fun removesOwned(userId: String)
    fun append(userId: String, content: String): String
    fun isOwners(feedIds: List<String>, userId: String): Boolean
}
