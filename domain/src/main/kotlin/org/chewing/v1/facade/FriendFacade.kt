package org.chewing.v1.facade

import org.chewing.v1.implementation.friend.friend.FriendAggregator
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShipProfile
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.springframework.stereotype.Service

@Service
class FriendFacade(
    private val friendShipService: FriendShipService,
    private val userService: UserService,
    private val friendAggregator: FriendAggregator,
) {

    fun createFriends(
        userId: UserId,
        friendShipProfiles: List<FriendShipProfile>,
    ): List<Friend> {
        val localPhoneNumbers = friendShipProfiles.map { it.localPhoneNumber }
        // 유저 정보 가져오기
        val targetUsers = userService.getUsersByContacts(localPhoneNumbers, AccessStatus.ACCESS)
        // 친구 추가 후 친구 정보 가져오기
        val user = userService.getUser(userId, AccessStatus.ACCESS)
        val friendShips = friendShipService.createFriendShips(userId, user, targetUsers, friendShipProfiles)

        val friends = friendAggregator.aggregates(targetUsers, friendShips)
        return friends
    }

    fun updateFriendStatus(userId: UserId, friendId: UserId, friendName: String) {
        friendShipService.checkAccessibleFriendShip(userId, friendId)
        friendShipService.changeFriendShipStatus(userId, friendId, friendName)
    }

    fun getFriends(userId: UserId): List<Friend> {
        val friendShips = friendShipService.getFavoriteFriendShips(userId)
        val friendIds = friendShips.map { it.friendId }
        val usersWithFeeds = userService.getUsers(friendIds)

        return friendShips
            .mapNotNull { friendShip ->
                val user = usersWithFeeds.find { it.info.userId == friendShip.friendId }
                user?.let { user ->
                    val friend = Friend.of(user, friendShip)
                    friend
                }
            }
    }
}
