package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.friend.Friend

data class FriendResponse(
    val friendId: String,
    val name: String,
    val imageUrl: String,
    val imageType: String,
    val statusMessage: String,
    val favorite: Boolean,
    val status: String,
) {
    companion object {
        fun of(
            friend: Friend,
        ): FriendResponse {
            return FriendResponse(
                friendId = friend.user.info.userId.id,
                name = friend.name,
                imageUrl = friend.user.info.image.url,
                imageType = friend.user.info.image.type.name.lowercase(),
                statusMessage = friend.user.info.statusMessage,
                favorite = friend.isFavorite,
                status = friend.status.name.lowercase(),
            )
        }
    }
}
