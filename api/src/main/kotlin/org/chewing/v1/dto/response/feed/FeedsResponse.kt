package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed

data class FeedsResponse(
    val feeds: List<MainFeedResponse>,
) {
    companion object {
        fun of(
            feeds: List<Feed>,
        ): FeedsResponse {
            return FeedsResponse(
                feeds = feeds.map {
                    MainFeedResponse.of(it)
                },
            )
        }
    }
}
