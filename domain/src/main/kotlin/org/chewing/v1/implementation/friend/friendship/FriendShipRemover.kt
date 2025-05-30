package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class FriendShipRemover(
    private val friendShipRepository: FriendShipRepository,
) {
    @Transactional
    fun remove(userId: UserId, friendId: UserId) {
        friendShipRepository.remove(userId, friendId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }

    @Transactional
    fun block(userId: UserId, friendId: UserId) {
        friendShipRepository.block(userId, friendId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
        friendShipRepository.blocked(friendId, userId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }
}
