package org.chewing.v1.service.friend

import org.chewing.v1.implementation.friend.friendship.*
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class FriendShipService(
    private val friendShipReader: FriendShipReader,
    private val friendShipRemover: FriendShipRemover,
    private val friendShipAppender: FriendShipAppender,
    private val friendShipValidator: FriendShipValidator,
    private val friendShipUpdater: FriendShipUpdater,
) {

    fun getAccessFriendShips(userId: UserId, sort: FriendSortCriteria): List<FriendShip> = friendShipReader.readsAccess(userId, sort)

    fun getAccessFriendShipsIn(friendIds: List<UserId>, userId: UserId): List<FriendShip> = friendShipReader.readsAccessIdIn(friendIds, userId)

    fun createFriendShip(userId: UserId, userName: String, friendId: UserId, friendName: String) {
        friendShipValidator.validateCreationAllowed(userId, friendId)
        friendShipAppender.appendFriend(userId, userName, friendId, friendName)
    }

    fun removeFriendShip(userId: UserId, friendId: UserId) {
        friendShipRemover.removeFriendShip(userId, friendId)
    }

    fun blockFriendShip(userId: UserId, friendId: UserId) {
        friendShipRemover.blockFriend(userId, friendId)
    }

    fun changeFriendFavorite(userId: UserId, friendId: UserId, favorite: Boolean) {
        // 친구인지 확인
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        // 친구 즐겨찾기 변경
        friendShipUpdater.updateFavorite(userId, friendId, favorite)
    }

    fun changeFriendName(userId: UserId, friendId: UserId, friendName: String) {
        // 친구인지 확인
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        // 친구 이름 변경
        friendShipUpdater.updateName(userId, friendId, friendName)
    }

    fun getFriendName(userId: UserId, friendId: UserId): String {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        return friendShip.friendName
    }
}
