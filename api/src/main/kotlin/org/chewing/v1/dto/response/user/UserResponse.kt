package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserStatus

data class UserResponse(
    val statusMessage: String,
    val statusEmoji: String,
    val imageUrl: String,
    val imageType: String,
    val name: String
) {
    companion object {
        fun of(
            user: User,
            userStatus: UserStatus,
        ): UserResponse {
            return UserResponse(
                statusMessage = userStatus.message,
                statusEmoji = userStatus.emoji,
                imageUrl = user.image.url,
                imageType = user.image.type.value().lowercase(),
                name = user.name,
            )
        }
    }
}
