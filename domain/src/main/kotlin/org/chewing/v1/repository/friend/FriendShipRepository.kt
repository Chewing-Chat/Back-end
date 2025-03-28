package org.chewing.v1.repository.friend

import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FriendShipRepository {

    fun reads(userId: UserId): List<FriendShip>
    fun appendIfNotExist(userId: UserId, targetUserId: UserId, targetUserName: String, status: FriendShipStatus): FriendShip

    fun remove(userId: UserId, friendId: UserId): UserId?
    fun block(userId: UserId, friendId: UserId): UserId?
    fun unblock(userId: UserId, friendId: UserId): UserId?
    fun blocked(userId: UserId, friendId: UserId): UserId?
    fun readByRelation(userId: UserId, friendId: UserId): FriendShip?
    fun readsByRelation(friendIds: List<UserId>, userId: UserId): List<FriendShip>
    fun readsFavorite(userId: UserId): List<FriendShip>
    fun updateFavorite(userId: UserId, friendId: UserId, favorite: Boolean): UserId?
    fun updateName(userId: UserId, friendId: UserId, friendName: String): UserId?
    fun allowedFriend(userId: UserId, friendId: UserId, friendName: String): UserId?
}
