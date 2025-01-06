package org.chewing.v1.model.feed

import org.chewing.v1.model.media.Media
class FeedDetail private constructor(
    val feedDetailId: String,
    val media: Media,
    val feedId: String,
) {
    companion object {
        fun of(feedDetailId: String, media: Media, feedId: String): FeedDetail {
            return FeedDetail(feedDetailId, media, feedId)
        }
    }
}
