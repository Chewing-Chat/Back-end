package org.chewing.v1.model.feed

import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

class FeedInfo private constructor(
    val feedId: FeedId,
    val content: String,
    val uploadAt: LocalDateTime,
    val userId: UserId,
    val type: FeedType,
) {
    companion object {
        fun of(
            feedId: FeedId,
            content: String,
            uploadAt: LocalDateTime,
            userId: UserId,
            type: FeedType,
        ): FeedInfo {
            return FeedInfo(feedId, content, uploadAt, userId, type)
        }
    }
}
