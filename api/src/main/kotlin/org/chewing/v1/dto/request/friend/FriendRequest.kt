package org.chewing.v1.dto.request.friend

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.user.UserName

class FriendRequest {
    data class AddWithEmail(
        val email: String,
        val firstName: String,
        val lastName: String,
    ) {
        fun toUserName(): UserName = UserName.of(firstName, lastName)
        fun toEmail(): EmailAddress = EmailAddress.of(email)
    }

    data class UpdateName(
        val friendId: String,
        val firstName: String,
        val lastName: String,
    ) {
        fun toFriendName(): UserName = UserName.of(firstName, lastName)
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
        val firstName: String,
        val lastName: String,
    ) {
        fun toUserName(): UserName = UserName.of(firstName, lastName)
        fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    }
}
