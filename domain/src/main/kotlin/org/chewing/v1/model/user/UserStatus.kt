package org.chewing.v1.model.user

class UserStatus private constructor(
    val statusId: String,
    val message: String,
    val emoji: String,
    val userId: String,
    val isSelected: Boolean,
) {
    companion object {
        fun of(
            statusId: String,
            userId: String,
            statusMessage: String,
            emoji: String,
            isSelected: Boolean,
        ): UserStatus = UserStatus(statusId, statusMessage, emoji, userId, isSelected)
        fun default(userId: String): UserStatus = UserStatus("none", "none", "none", userId, true)
    }
}
