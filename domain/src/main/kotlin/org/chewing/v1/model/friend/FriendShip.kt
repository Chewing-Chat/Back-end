package org.chewing.v1.model.friend

import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId

class FriendShip private constructor(
    val friendId: UserId,
    val friendName: String,
    val isFavorite: Boolean,
    val type: AccessStatus,
) {
    companion object {
        fun of(friendId: UserId, friendName: String, isFavorite: Boolean, type: AccessStatus): FriendShip {
            return FriendShip(
                friendId = friendId,
                friendName = friendName,
                isFavorite = isFavorite,
                type,
            )
        }
    }
}
