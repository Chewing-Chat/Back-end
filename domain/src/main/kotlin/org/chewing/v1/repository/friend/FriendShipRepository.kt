package org.chewing.v1.repository.friend

import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Repository

@Repository
interface FriendShipRepository {

    fun readsAccess(userId: UserId, accessStatus: AccessStatus, sort: FriendSortCriteria): List<FriendShip>
    fun reads(friendIds: List<UserId>, userId: UserId, accessStatus: AccessStatus): List<FriendShip>
    fun append(userId: UserId, targetUserId: UserId, targetUserName: String)

    fun remove(userId: UserId, friendId: UserId): UserId?
    fun block(userId: UserId, friendId: UserId): UserId?
    fun blocked(userId: UserId, friendId: UserId): UserId?
    fun read(userId: UserId, friendId: UserId): FriendShip?
    fun updateFavorite(userId: UserId, friendId: UserId, favorite: Boolean): UserId?
    fun updateName(userId: UserId, friendId: UserId, friendName: String): UserId?
}
