package org.chewing.v1.dto.response.feed

import org.chewing.v1.model.feed.Feed
import java.time.format.DateTimeFormatter

data class FeedResponse(
    val feedId: String,
    val uploadTime: String,
    val topic: String,
    val details: List<FeedDetailResponse>,
) {
    companion object {
        fun of(
            feed: Feed,
        ): FeedResponse {
            val formattedUploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
            return FeedResponse(
                feedId = feed.feed.feedId,
                uploadTime = formattedUploadTime,
                topic = feed.feed.topic,
                details = feed.feedDetails.map { FeedDetailResponse.of(it) },
            )
        }
    }
}
