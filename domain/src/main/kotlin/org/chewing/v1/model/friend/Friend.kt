package org.chewing.v1.model.friend

import org.chewing.v1.model.user.User

class Friend private constructor(
    val user: User,
    val isFavorite: Boolean,
    val name: String,
    val status: FriendShipStatus,
) {
    companion object {
        fun of(
            friend: User,
            favorite: Boolean,
            friendName: String,
            status: FriendShipStatus,
        ): Friend {
            return Friend(
                user = friend,
                isFavorite = favorite,
                name = friendName,
                status = status,
            )
        }
    }
}
