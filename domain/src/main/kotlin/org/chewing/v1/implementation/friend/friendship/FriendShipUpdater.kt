package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.user.UserName
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipUpdater(
    private val friendShipRepository: FriendShipRepository,
) {
    fun updateFavorite(userId: String, friendId: String, favorite: Boolean) {
        friendShipRepository.updateFavorite(userId, friendId, favorite) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }

    fun updateName(userId: String, friendId: String, friendName: UserName) {
        friendShipRepository.updateName(userId, friendId, friendName) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    }
}
