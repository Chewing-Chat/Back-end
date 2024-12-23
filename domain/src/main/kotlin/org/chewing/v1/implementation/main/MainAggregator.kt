package org.chewing.v1.implementation.main

import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserStatus
import org.springframework.stereotype.Component

@Component
class MainAggregator {

    fun aggregates(friendShips: List<FriendShip>, users: List<User>, userStatuses: List<UserStatus>): List<Friend> {
        val userMap = users.associateBy { it.userId }
        val statusMap = userStatuses.associateBy { it.userId }

        return friendShips.mapNotNull { friendInfo ->
            val user = userMap[friendInfo.friendId]
            val status = statusMap[friendInfo.friendId]
            status?.let {
                Friend.of(user!!, friendInfo.isFavorite, friendInfo.friendName, it, friendInfo.type)
            }
        }
    }
}
