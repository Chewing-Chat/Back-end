package org.chewing.v1.repository.feed

import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository {
    fun read(feedId: String): FeedInfo?
    fun reads(userId: UserId): List<FeedInfo>
    fun readsFriendBetween(userId: UserId, dateTarget: DateTarget): List<FeedInfo>
    fun removes(feedIds: List<String>)
    fun removesOwned(userId: UserId)
    fun append(userId: UserId, content: String): String
    fun isOwners(feedIds: List<String>, userId: UserId): Boolean
}
