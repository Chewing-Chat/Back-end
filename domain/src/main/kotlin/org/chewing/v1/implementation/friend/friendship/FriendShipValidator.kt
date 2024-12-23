package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipValidator(
    private val friendShipRepository: FriendShipRepository,
) {
    private fun validateBlock(friendShip: FriendShip) {
        if (friendShip.type == AccessStatus.BLOCK) {
            throw ConflictException(ErrorCode.FRIEND_BLOCK)
        }
    }

    private fun validateBlocked(friendShip: FriendShip) {
        if (friendShip.type == AccessStatus.BLOCKED) {
            throw ConflictException(ErrorCode.FRIEND_BLOCKED)
        }
    }

    private fun validateMyself(userId: String, friendId: String) {
        if (userId == friendId) {
            throw ConflictException(ErrorCode.FRIEND_MYSELF)
        }
    }

    fun validateCreationAllowed(userId: String, friendId: String) {
        validateMyself(userId, friendId)
        friendShipRepository.read(userId, friendId)?.let {
            validateBlock(it)
            validateBlocked(it)
            throw ConflictException(ErrorCode.FRIEND_ALREADY_CREATED)
        }
    }

    fun validateInteractionAllowed(friendShip: FriendShip) {
        validateBlock(friendShip)
        validateBlocked(friendShip)
    }
}
