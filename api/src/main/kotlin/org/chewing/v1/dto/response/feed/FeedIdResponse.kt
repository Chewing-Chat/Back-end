package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.FeedId

data class FeedIdResponse(
    val feedId: String,
) {
    companion object {
        fun of(
            feedId: FeedId,
        ): FeedIdResponse {
            return FeedIdResponse(feedId.id)
        }
    }
}
