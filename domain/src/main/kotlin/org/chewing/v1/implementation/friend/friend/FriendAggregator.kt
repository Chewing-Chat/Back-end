package org.chewing.v1.implementation.friend.friend

import org.chewing.v1.model.friend.Friend
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.User
import org.springframework.stereotype.Component

@Component
class FriendAggregator {

    fun aggregates(
        users: List<User>,
        friendShips: List<FriendShip>,
    ): List<Friend> {
        val userById = users.associateBy { it.info.userId }
        return friendShips.mapNotNull { friendShip ->
            val matchedUser = userById[friendShip.friendId]
            matchedUser?.let { user ->
                Friend.of(user, friendShip.isFavorite, friendShip.friendName, friendShip.status)
            }
        }
    }
}
