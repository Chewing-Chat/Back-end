package org.chewing.v1.model.announcement

import java.time.LocalDateTime

class Announcement private constructor(
    val id: String,
    val topic: String,
    val uploadAt: LocalDateTime,
    val content: String,
) {
    companion object {
        fun of(
            id: String,
            topic: String,
            uploadTime: LocalDateTime,
            content: String,
        ): Announcement {
            return Announcement(
                id,
                topic,
                uploadTime,
                content,
            )
        }
    }
}
