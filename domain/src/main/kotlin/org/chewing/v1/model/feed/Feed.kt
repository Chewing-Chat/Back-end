package org.chewing.v1.model.feed

class Feed private constructor(
    val feed: FeedInfo,
    val feedDetails: List<FeedDetail>,
) {
    companion object {
        fun of(
            feed: FeedInfo,
            feedDetails: List<FeedDetail>,
        ): Feed {
            return Feed(feed, feedDetails)
        }
    }
}
