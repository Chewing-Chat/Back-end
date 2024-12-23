package org.chewing.v1.facade

import org.chewing.v1.implementation.main.MainAggregator
import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserStatus
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.chewing.v1.service.user.UserStatusService
import org.springframework.stereotype.Service

@Service
class MainFacade(
    private val userService: UserService,
    private val userStatusService: UserStatusService,
    private val friendShipService: FriendShipService,
    private val mainAggregator: MainAggregator,
) {
    fun getMainPage(userId: String, sort: FriendSortCriteria): Triple<User, UserStatus, List<Friend>> {
        val user = userService.getAccessUser(userId)
        val userStatus = userStatusService.getSelectedStatus(userId)
        val friendShips = friendShipService.getAccessFriendShips(userId, sort)
        val friendIds = friendShips.map { it.friendId }
        val users = userService.getUsers(friendIds)
        val usersStatuses = userStatusService.getSelectedStatuses(friendIds)
        val friends = mainAggregator.aggregates(friendShips, users, usersStatuses)
        return Triple(user, userStatus, friends)
    }
}
