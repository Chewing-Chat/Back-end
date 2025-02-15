package org.chewing.v1.repository.feed

import org.chewing.v1.model.ai.DateTarget
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedInfo
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FeedRepository {
    fun read(feedId: FeedId): FeedInfo?
    fun reads(userId: UserId): List<FeedInfo>
    fun readsFriendBetween(userId: UserId, dateTarget: DateTarget): List<FeedInfo>
    fun removes(feedIds: List<FeedId>)
    fun removesOwned(userId: UserId)
    fun append(userId: UserId, content: String): FeedId
    fun isOwners(feedIds: List<FeedId>, userId: UserId): Boolean
    fun readsOneDay(targetUserIds: List<UserId>): List<FeedInfo>
}
