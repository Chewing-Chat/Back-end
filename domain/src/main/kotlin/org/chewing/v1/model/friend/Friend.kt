package org.chewing.v1.model.friend

import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserName
import org.chewing.v1.model.user.UserStatus

class Friend private constructor(
    val user: User,
    val isFavorite: Boolean,
    val name: UserName,
    val status: UserStatus,
    val type: AccessStatus,
) {
    companion object {
        fun of(
            friend: User,
            favorite: Boolean,
            friendName: UserName,
            friendStatus: UserStatus,
            type: AccessStatus,
        ): Friend {
            return Friend(
                user = friend,
                isFavorite = favorite,
                name = friendName,
                status = friendStatus,
                type = type,
            )
        }
    }
}
