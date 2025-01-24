package org.chewing.v1.dto.response.user

import org.chewing.v1.model.user.UserInfo

data class UserResponse(
    val statusMessage: String,
    val imageUrl: String,
    val imageType: String,
    val name: String,
) {
    companion object {
        fun of(
            userInfo: UserInfo,
        ): UserResponse {
            return UserResponse(
                statusMessage = userInfo.statusMessage,
                imageUrl = userInfo.image.url,
                imageType = userInfo.image.type.value().lowercase(),
                name = userInfo.name,
            )
        }
    }
}
