package org.chewing.v1.model.feed

import java.time.LocalDateTime

class FeedInfo private constructor(
    val feedId: String,
    val topic: String,
    val uploadAt: LocalDateTime,
    val userId: String,
) {
    companion object {
        fun of(
            feedId: String,
            topic: String,
            uploadAt: LocalDateTime,
            userId: String,
        ): FeedInfo {
            return FeedInfo(feedId, topic, uploadAt, userId)
        }
    }
}
