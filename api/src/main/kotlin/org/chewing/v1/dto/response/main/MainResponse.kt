package org.chewing.v1.dto.response.main

import org.chewing.v1.dto.response.user.UserResponse
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.user.User

data class MainResponse(
    val friends: List<FriendMainResponse>,
    val user: UserResponse,
    val totalFriends: Int,
) {
    data class FriendMainResponse(
        val friendId: String,
        val name: String,
        val imageUrl: String,
        val imageType: String,
        val access: String,
        val favorite: Boolean,
        val statusMessage: String,
    ) {
        companion object {
            fun of(friend: Friend): FriendMainResponse {
                return FriendMainResponse(
                    friendId = friend.user.userId.id,
                    name = friend.name,
                    imageUrl = friend.user.image.url,
                    imageType = friend.user.image.type.value().lowercase(),
                    statusMessage = friend.user.statusMessage,
                    favorite = friend.isFavorite,
                    access = friend.user.status.name.lowercase(),
                )
            }
        }
    }

    companion object {
        fun ofList(user: User, friends: List<Friend>): MainResponse {
            return MainResponse(
                friends = friends.map { FriendMainResponse.of(it) },
                user = UserResponse.of(user),
                totalFriends = friends.size,
            )
        }
    }
}
