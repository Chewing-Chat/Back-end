package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipAppender(
    private val friendShipRepository: FriendShipRepository,
) {
    fun appendIfNotExist(userId: UserId, friendId: UserId, friendName: String, status: FriendShipStatus): FriendShip {
        return friendShipRepository.appendIfNotExist(userId, friendId, friendName, status)
    }
}
