package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.springframework.stereotype.Component

@Component
class FriendShipValidator() {
    private fun validateBlock(friendShip: FriendShip) {
        if (friendShip.status == FriendShipStatus.BLOCK) {
            throw ConflictException(ErrorCode.FRIEND_BLOCK)
        }
    }

    private fun validateBlocked(friendShip: FriendShip) {
        if (friendShip.status == FriendShipStatus.BLOCKED) {
            throw ConflictException(ErrorCode.FRIEND_BLOCKED)
        }
    }

    private fun validateDeleted(friendShip: FriendShip) {
        if (friendShip.status == FriendShipStatus.DELETE) {
            throw ConflictException(ErrorCode.FRIEND_DELETED)
        }
    }

    private fun validateNormal(friendShip: FriendShip) {
        if (friendShip.status == FriendShipStatus.NORMAL) {
            throw ConflictException(ErrorCode.FRIEND_NORMAL)
        }
    }

    fun validateInteractionAllowed(friendShip: FriendShip) {
        validateBlock(friendShip)
        validateBlocked(friendShip)
        validateDeleted(friendShip)
        validateNormal(friendShip)
    }

    fun validateAllowedFriend(friendShip: FriendShip) {
        validateBlock(friendShip)
        validateBlocked(friendShip)
        validateDeleted(friendShip)
    }
}
