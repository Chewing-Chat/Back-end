package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserName

data class FriendInfoResponse(
    val friendId: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String,
    val imageType: String,
    val access: String,
) {
    companion object {
        fun of(
            friendId: String,
            userName: UserName,
            imageUrl: String,
            imageType: String,
            access: AccessStatus,
        ): FriendInfoResponse {
            return FriendInfoResponse(
                friendId = friendId,
                firstName = userName.firstName(),
                lastName = userName.lastName(),
                imageUrl = imageUrl,
                imageType = imageType,
                access = access.name.lowercase(),
            )
        }
    }
}
