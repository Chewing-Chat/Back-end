package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed

data class ThumbnailFeedsResponse(
    val feeds: List<ThumbnailFeedResponse>,
) {
    companion object {
        fun of(
            feeds: List<Feed>,
        ): ThumbnailFeedsResponse {
            return ThumbnailFeedsResponse(
                feeds = feeds.sortedByDescending {
                    it.feed.uploadAt
                }.map { ThumbnailFeedResponse.of(it) },
            )
        }
    }
}
