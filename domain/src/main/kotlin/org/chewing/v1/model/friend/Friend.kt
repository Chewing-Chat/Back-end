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
            friendShip: FriendShip
        ): Friend {
            return Friend(
                user = friend,
                isFavorite = friendShip.isFavorite,
                name = friendShip.friendName,
                status = friendShip.status,
            )
        }
    }
}
