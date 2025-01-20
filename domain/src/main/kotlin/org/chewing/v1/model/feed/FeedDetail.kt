package org.chewing.v1.model.feed

import org.chewing.v1.model.media.Media
class FeedDetail private constructor(
    val feedDetailId: FeedDetailId,
    val media: Media,
    val feedId: FeedId,
) {
    companion object {
        fun of(feedDetailId: FeedDetailId, media: Media, feedId: FeedId): FeedDetail {
            return FeedDetail(feedDetailId, media, feedId)
        }
    }
}
