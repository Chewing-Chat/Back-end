package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.user.AccessStatus

data class FriendInfoResponse(
    val friendId: String,
    val name: String,
    val imageUrl: String,
    val imageType: String,
    val access: String,
) {
    companion object {
        fun of(
            friendId: String,
            userName: String,
            imageUrl: String,
            imageType: String,
            access: AccessStatus,
        ): FriendInfoResponse {
            return FriendInfoResponse(
                friendId = friendId,
                name = userName,
                imageUrl = imageUrl,
                imageType = imageType,
                access = access.name.lowercase(),
            )
        }
    }
}
