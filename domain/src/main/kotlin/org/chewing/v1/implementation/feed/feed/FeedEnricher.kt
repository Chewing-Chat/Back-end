package org.chewing.v1.implementation.feed.feed

import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.feed.FeedDetail
import org.chewing.v1.model.feed.FeedInfo
import org.springframework.stereotype.Component

@Component
class FeedEnricher {
    fun enriches(
        feeds: List<FeedInfo>,
        visibleFeedIds: List<String>,
        feedDetails: List<FeedDetail>,
    ): List<Feed> {
        val visibleFeeds = feeds.filter { visibleFeedIds.contains(it.feedId) }
        val feedDetailMap = feedDetails.groupBy { it.feedId }
        return visibleFeeds.map { feedInfo ->
            val details = feedDetailMap[feedInfo.feedId] ?: emptyList()
            Feed.of(
                feedInfo,
                details,
            )
        }
    }
}
