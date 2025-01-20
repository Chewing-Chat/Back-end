package org.chewing.v1.model.user

import java.time.LocalDateTime

class UserEmoticonPackInfo private constructor(
    val userId: UserId,
    val emoticonPackId: String,
    val createAt: LocalDateTime,
) {
    companion object {
        fun of(
            userId: UserId,
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
