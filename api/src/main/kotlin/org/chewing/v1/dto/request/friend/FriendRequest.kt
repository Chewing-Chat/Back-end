package org.chewing.v1.dto.request.friend

import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.friend.FriendShipProfile
import org.chewing.v1.model.user.UserId

class FriendRequest {

    data class UpdateName(
        val friendId: String,
        val name: String,
    ) {
        fun toFriendName(): String = name
        fun toFriendId(): UserId = UserId.of(friendId)
    }

    data class UpdateFavorite(
        val friendId: String,
        val favorite: Boolean,
    )

    data class Delete(
        val friendId: String,
    )

    data class Block(
        val friendId: String,
    )

    data class Create(
        val countryCode: String,
        val phoneNumber: String,
        val name: String,
    ) {
        fun toFriendShipProfile(): FriendShipProfile {
            return FriendShipProfile.of(
                localPhoneNumber = LocalPhoneNumber.of(
                    number = phoneNumber,
                    countryCode = countryCode,
                ),
                friendName = name,
            )
        }
    }
}
