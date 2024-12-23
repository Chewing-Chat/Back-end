package org.chewing.v1.service.friend

import org.chewing.v1.implementation.friend.friendship.*
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.UserName
import org.springframework.stereotype.Service

@Service
class FriendShipService(
    private val friendShipReader: FriendShipReader,
    private val friendShipRemover: FriendShipRemover,
    private val friendShipAppender: FriendShipAppender,
    private val friendShipValidator: FriendShipValidator,
    private val friendShipUpdater: FriendShipUpdater,
) {

    fun getAccessFriendShips(userId: String, sort: FriendSortCriteria): List<FriendShip> = friendShipReader.readsAccess(userId, sort)

    fun getAccessFriendShipsIn(friendIds: List<String>, userId: String): List<FriendShip> = friendShipReader.readsAccessIdIn(friendIds, userId)

    fun createFriendShip(userId: String, userName: UserName, friendId: String, friendName: UserName) {
        friendShipValidator.validateCreationAllowed(userId, friendId)
        friendShipAppender.appendFriend(userId, userName, friendId, friendName)
    }

    fun removeFriendShip(userId: String, friendId: String) {
        friendShipRemover.removeFriendShip(userId, friendId)
    }

    fun blockFriendShip(userId: String, friendId: String) {
        friendShipRemover.blockFriend(userId, friendId)
    }

    fun changeFriendFavorite(userId: String, friendId: String, favorite: Boolean) {
        // 친구인지 확인
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        // 친구 즐겨찾기 변경
        friendShipUpdater.updateFavorite(userId, friendId, favorite)
    }

    fun changeFriendName(userId: String, friendId: String, friendName: UserName) {
        // 친구인지 확인
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        // 친구 이름 변경
        friendShipUpdater.updateName(userId, friendId, friendName)
    }

    fun getFriendName(userId: String, friendId: String): UserName {
        val friendShip = friendShipReader.read(userId, friendId)
        friendShipValidator.validateInteractionAllowed(friendShip)
        return friendShip.friendName
    }
}
