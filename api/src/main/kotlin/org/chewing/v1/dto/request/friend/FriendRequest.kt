package org.chewing.v1.dto.request.friend

import org.chewing.v1.model.auth.PhoneNumber

class FriendRequest {

    data class UpdateName(
        val friendId: String,
        val name: String,
    ) {
        fun toFriendName(): String = name
        fun toFriendId(): String = friendId
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

    data class AddWithPhone(
        val countryCode: String,
        val phoneNumber: String,
        val name: String,
    ) {
        fun toUserName(): String = name
        fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    }
}
