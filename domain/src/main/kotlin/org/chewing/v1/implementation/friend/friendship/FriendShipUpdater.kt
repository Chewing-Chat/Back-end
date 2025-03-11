package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipUpdater(
    private val friendShipRepository: FriendShipRepository,
) {
    fun updateFavorite(userId: UserId, friendId: UserId, favorite: Boolean) {
        friendShipRepository.updateFavorite(userId, friendId, favorite) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }

    fun updateName(userId: UserId, friendId: UserId, friendName: String) {
        friendShipRepository.updateName(userId, friendId, friendName) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }
    fun allowedFriend(userId: UserId, friendId: UserId, friendName: String) {
        friendShipRepository.allowedFriend(userId, friendId, friendName) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }

    fun unblock(userId: UserId, friendId: UserId) {
        friendShipRepository.unblock(userId, friendId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }
}
