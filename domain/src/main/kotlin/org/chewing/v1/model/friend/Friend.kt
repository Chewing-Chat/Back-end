package org.chewing.v1.model.friend

import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User

class Friend private constructor(
    val user: User,
    val isFavorite: Boolean,
    val name: String,
    val type: AccessStatus,
) {
    companion object {
        fun of(
            friend: User,
            favorite: Boolean,
            friendName: String,
            type: AccessStatus,
        ): Friend {
            return Friend(
                user = friend,
                isFavorite = favorite,
                name = friendName,
                type = type,
            )
        }
    }
}
