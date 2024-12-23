package org.chewing.v1.model.feed

import java.time.LocalDateTime

class FeedInfo private constructor(
    val feedId: String,
    val topic: String,
    val likes: Int,
    val comments: Int,
    val uploadAt: LocalDateTime,
    val userId: String,
) {
    companion object {
        fun of(
            feedId: String,
            topic: String,
            likes: Int,
            comments: Int,
            uploadAt: LocalDateTime,
            userId: String,
        ): FeedInfo {
            return FeedInfo(feedId, topic, likes, comments, uploadAt, userId)
        }
    }
}
