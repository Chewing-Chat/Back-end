package org.chewing.v1.dto.response.main

import org.chewing.v1.dto.response.user.UserResponse
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserStatus

data class MainResponse(
    val friends: List<FriendMainResponse>,
    val user: UserResponse,
    val totalFriends: Int,
) {
    data class FriendMainResponse(
        val friendId: String,
        val firstName: String,
        val lastName: String,
        val backgroundImageUrl: String,
        val backgroundImageType: String,
        val imageUrl: String,
        val imageType: String,
        val access: String,
        val favorite: Boolean,
        val statusMessage: String,
        val statusEmoji: String,
    ) {
        companion object {
            fun of(friend: Friend): FriendMainResponse {
                return FriendMainResponse(
                    friendId = friend.user.userId,
                    firstName = friend.name.firstName(),
                    lastName = friend.name.lastName(),
                    imageUrl = friend.user.image.url,
                    imageType = friend.user.image.type.value().lowercase(),
                    statusMessage = friend.status.message,
                    favorite = friend.isFavorite,
                    access = friend.user.status.name.lowercase(),
                    backgroundImageUrl = friend.user.backgroundImage.url,
                    backgroundImageType = friend.user.backgroundImage.type.value().lowercase(),
                    statusEmoji = friend.status.emoji,
                )
            }
        }
    }

    companion object {
        fun ofList(user: User, userStatus: UserStatus, friends: List<Friend>): MainResponse {
            return MainResponse(
                friends = friends.map { FriendMainResponse.of(it) },
                user = UserResponse.of(user, userStatus),
                totalFriends = friends.size,
            )
        }
    }
}
