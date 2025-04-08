package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.friend.Friend

data class FriendResponse(
    val friendId: String,
    val name: String,
    val profileImageUrl: String,
    val profileImageType: String,
    val phoneNumber: String,
    val countryCode: String,
    val statusMessage: String,
    val favorite: Boolean,
    val status: String,
    val birthday: String,
) {
    companion object {
        fun of(
            friend: Friend,
        ): FriendResponse {
            return FriendResponse(
                friendId = friend.user.info.userId.id,
                name = friend.name,
                profileImageUrl = friend.user.info.image.url,
                profileImageType = friend.user.info.image.type.value(),
                statusMessage = friend.user.info.statusMessage,
                favorite = friend.isFavorite,
                status = friend.status.name.lowercase(),
                phoneNumber = friend.user.localPhoneNumber.number,
                countryCode = friend.user.localPhoneNumber.countryCode,
                birthday = friend.user.info.birthday?.toString() ?: "",
            )
        }
    }
}
