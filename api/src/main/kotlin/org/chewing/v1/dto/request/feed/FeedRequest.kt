package org.chewing.v1.dto.request.feed

import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId

class FeedRequest {
    data class Delete(
        val feedId: String,
    ) {
        fun toFeedId(): FeedId {
            return FeedId.of(feedId)
        }
    }
    data class CreateText(
        val content: String,
        val type: String,
        val friendIds: List<String>,
    ) {
        fun toType(): FeedType {
            return FeedType.valueOf(type.uppercase())
        }
        fun toFriendIds(): List<UserId> {
            return friendIds.map { UserId.of(it) }
        }
        fun toContent(): String {
            return content
        }
    }
}
