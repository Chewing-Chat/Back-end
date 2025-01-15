package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.User

data class UserResponse(
    val statusMessage: String,
    val imageUrl: String,
    val imageType: String,
    val name: String,
) {
    companion object {
        fun of(
            user: User,
        ): UserResponse {
            return UserResponse(
                statusMessage = user.statusMessage,
                imageUrl = user.image.url,
                imageType = user.image.type.value().lowercase(),
                name = user.name,
            )
        }
    }
}
