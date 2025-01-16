package org.chewing.v1.model.feed

import java.time.LocalDateTime

class FeedInfo private constructor(
    val feedId: String,
    val content: String,
    val uploadAt: LocalDateTime,
    val userId: String,
) {
    companion object {
        fun of(
            feedId: String,
            content: String,
            uploadAt: LocalDateTime,
            userId: String,
        ): FeedInfo {
            return FeedInfo(feedId, content, uploadAt, userId)
        }
    }
}
