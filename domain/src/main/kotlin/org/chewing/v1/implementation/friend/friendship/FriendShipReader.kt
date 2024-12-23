package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipReader(
    private val friendShipRepository: FriendShipRepository,
) {
    fun readsAccess(userId: String, sort: FriendSortCriteria): List<FriendShip> = friendShipRepository.readsAccess(userId, AccessStatus.ACCESS, sort)
    fun read(userId: String, friendId: String): FriendShip = friendShipRepository.read(userId, friendId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    fun readsAccessIdIn(friendIds: List<String>, userId: String): List<FriendShip> = friendShipRepository.reads(friendIds, userId, AccessStatus.ACCESS)
}
