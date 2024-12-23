package org.chewing.v1.dto.request.feed

import org.chewing.v1.model.feed.FeedTarget

class LikesRequest {
    data class Add(
        val feedId: String,
    ) {
        fun toFeedId(): String = feedId
        fun toTarget(): FeedTarget = FeedTarget.LIKES
    }

    data class Delete(
        val feedId: String,
    ) {
        fun toFeedId(): String = feedId
        fun toUpdateType(): FeedTarget = FeedTarget.UNLIKES
    }
}
