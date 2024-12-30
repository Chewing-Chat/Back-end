package org.chewing.v1.dto.response.friend

import org.chewing.v1.model.friend.FriendShip

data class FriendShipResponse(
    val friendId: String,
    val name: String,
    val favorite: Boolean,
) {
    companion object {
        fun of(friendShip: FriendShip): FriendShipResponse {
            return FriendShipResponse(
                friendId = friendShip.friendId,
                name = friendShip.friendName,
                favorite = friendShip.isFavorite,
            )
        }
    }
}
