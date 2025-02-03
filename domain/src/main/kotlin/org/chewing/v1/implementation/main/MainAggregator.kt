package org.chewing.v1.implementation.main

import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class MainAggregator {

    fun aggregates(friendShips: List<FriendShip>, userInfos: List<User>): List<Friend> {
        val userMap = userInfos.associateBy { it.info.userId }
        return friendShips.map { friendInfo ->
            val user = userMap[friendInfo.friendId]
            Friend.of(user!!, friendInfo.isFavorite, friendInfo.friendName, friendInfo.status)
        }
    }
}
