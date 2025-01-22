package org.chewing.v1.implementation.friend.friendship

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.friend.FriendShipRepository
import org.springframework.stereotype.Component

@Component
class FriendShipReader(
    private val friendShipRepository: FriendShipRepository,
) {
    fun readsAccess(userId: UserId, sort: FriendSortCriteria): List<FriendShip> = friendShipRepository.readsAccess(userId, AccessStatus.ACCESS, sort)
    fun read(userId: UserId, friendId: UserId): FriendShip = friendShipRepository.read(userId, friendId) ?: throw NotFoundException(ErrorCode.FRIEND_NOT_FOUND)
    fun readsAccessIdIn(friendIds: List<UserId>, userId: UserId): List<FriendShip> = friendShipRepository.reads(friendIds, userId, AccessStatus.ACCESS)
}
