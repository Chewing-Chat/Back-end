package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed

data class MainFeedResponse(
    val feedId: String,
    val mainDetailFileUrl: String,
    val type: String,
) {
    companion object {
        fun of(
            feed: Feed,
        ): MainFeedResponse {
            return MainFeedResponse(
                feedId = feed.feed.feedId,
                mainDetailFileUrl = feed.feedDetails[0].media.url,
                type = feed.feedDetails[0].media.type.value().lowercase(),
            )
        }
    }
}
