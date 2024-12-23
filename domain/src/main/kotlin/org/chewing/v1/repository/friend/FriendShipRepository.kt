package org.chewing.v1.repository.friend

import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserName
import org.springframework.stereotype.Repository

@Repository
interface FriendShipRepository {

    fun readsAccess(userId: String, accessStatus: AccessStatus, sort: FriendSortCriteria): List<FriendShip>
    fun reads(friendIds: List<String>, userId: String, accessStatus: AccessStatus): List<FriendShip>
    fun append(userId: String, targetUserId: String, targetUserName: UserName)

    fun remove(userId: String, friendId: String): String?
    fun block(userId: String, friendId: String): String?
    fun blocked(userId: String, friendId: String): String?
    fun read(userId: String, friendId: String): FriendShip?
    fun updateFavorite(userId: String, friendId: String, favorite: Boolean): String?
    fun updateName(userId: String, friendId: String, friendName: UserName): String?
}
