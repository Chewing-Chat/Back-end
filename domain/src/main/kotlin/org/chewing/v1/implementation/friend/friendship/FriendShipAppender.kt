package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipAppender(
    private val friendShipRepository: FriendShipRepository,
) {
    fun appendFriend(userId: UserId, userName: String, friendId: UserId, friendName: String) {
        friendShipRepository.append(userId, friendId, friendName)
        friendShipRepository.append(friendId, userId, userName)
    }
}
