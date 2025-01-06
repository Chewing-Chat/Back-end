package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipAppender(
    private val friendShipRepository: FriendShipRepository,
) {
    fun appendFriend(userId: String, userName: String, friendId: String, friendName: String) {
        friendShipRepository.append(userId, friendId, friendName)
        friendShipRepository.append(friendId, userId, userName)
    }
}
