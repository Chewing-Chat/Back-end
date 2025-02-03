package org.chewing.v1.dto.request.feed

import org.chewing.v1.model.feed.FeedId

class FeedRequest {
    data class Delete(
        val feedId: String,
    ) {
        fun toFeedId(): FeedId {
            return FeedId.of(feedId)
        }
    }
}
