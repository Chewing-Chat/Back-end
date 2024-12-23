package org.chewing.v1.dto.request.feed

class FeedRequest {
    data class Delete(
        val feedId: String,
    ) {
        fun toFeedId(): String = feedId
    }

    data class Hide(
        val feedId: String,
    ) {
        fun toFeedId(): String = feedId
    }
}
