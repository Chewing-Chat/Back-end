package org.chewing.v1.model.friend

import org.chewing.v1.model.user.UserId

class FriendShip private constructor(
    val userId: UserId,
    val friendId: UserId,
    val friendName: String,
    val isFavorite: Boolean,
    val status: FriendShipStatus,
) {
    companion object {
        fun of(userId: UserId, friendId: UserId, friendName: String, isFavorite: Boolean, status: FriendShipStatus): FriendShip {
            return FriendShip(
                friendId = friendId,
                userId = userId,
                friendName = friendName,
                isFavorite = isFavorite,
                status = status,
            )
        }
    }
}
