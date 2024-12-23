package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed
import java.time.format.DateTimeFormatter

data class OwnedFeedResponse(
    val feedId: String,
    val likes: Int,
    val uploadTime: String,
    val topic: String,
    val comments: Int,
    val liked: Boolean,
    val details: List<FeedDetailResponse>,
) {
    companion object {
        fun of(
            feed: Feed,
            isLiked: Boolean,
        ): OwnedFeedResponse {
            val formattedUploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
            return OwnedFeedResponse(
                feedId = feed.feed.feedId,
                likes = feed.feed.likes,
                uploadTime = formattedUploadTime,
                topic = feed.feed.topic,
                comments = feed.feed.comments,
                liked = isLiked,
                details = feed.feedDetails.map { FeedDetailResponse.of(it) },
            )
        }
    }
}
