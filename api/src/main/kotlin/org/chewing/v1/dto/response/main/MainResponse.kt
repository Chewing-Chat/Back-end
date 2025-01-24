package org.chewing.v1.dto.response.main

import org.chewing.v1.dto.response.user.UserResponse
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.user.UserInfo

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
                    friendId = friend.user.info.userId.id,
                    name = friend.name,
                    imageUrl = friend.user.info.image.url,
                    imageType = friend.user.info.image.type.value().lowercase(),
                    statusMessage = friend.user.info.statusMessage,
                    favorite = friend.isFavorite,
                    access = friend.user.info.status.name.lowercase(),
                )
            }
        }
    }

    companion object {
        fun ofList(userInfo: UserInfo, friends: List<Friend>): MainResponse {
            return MainResponse(
                friends = friends.map { FriendMainResponse.of(it) },
                user = UserResponse.of(userInfo),
                totalFriends = friends.size,
            )
        }
    }
}
