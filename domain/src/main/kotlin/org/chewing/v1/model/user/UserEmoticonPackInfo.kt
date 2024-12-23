package org.chewing.v1.model.user

import java.time.LocalDateTime

class UserEmoticonPackInfo private constructor(
    val userId: String,
    val emoticonPackId: String,
    val createAt: LocalDateTime,
) {
    companion object {
        fun of(
            userId: String,
            emoticonPackId: String,
            createAt: LocalDateTime,
        ): UserEmoticonPackInfo {
            return UserEmoticonPackInfo(
                userId = userId,
                emoticonPackId = emoticonPackId,
                createAt = createAt,
            )
        }
    }
}
