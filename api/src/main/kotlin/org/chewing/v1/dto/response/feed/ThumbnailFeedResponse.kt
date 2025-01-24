package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed

data class ThumbnailFeedResponse(
    val feedId: String,
    val thumbnailFileUrl: String,
    val type: String,
) {
    companion object {
        fun of(
            feed: Feed,
        ): ThumbnailFeedResponse {
            return ThumbnailFeedResponse(
                feedId = feed.feed.feedId.id,
                thumbnailFileUrl = feed.feedDetails[0].media.url,
                type = feed.feedDetails[0].media.type.value().lowercase(),
            )
        }
    }
}
