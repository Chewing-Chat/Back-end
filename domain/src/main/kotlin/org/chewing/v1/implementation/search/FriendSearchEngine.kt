package org.chewing.v1.implementation.search

import org.chewing.v1.model.friend.FriendShip
import org.springframework.stereotype.Component

@Component
class FriendSearchEngine() {
    fun search(friendShips: List<FriendShip>, keyword: String): List<FriendShip> {
        return personalized(friendShips, cleanKeyword(keyword))
    }

    private fun cleanKeyword(keyword: String): String = keyword.replace(" ", "")

    private fun personalized(friendShips: List<FriendShip>, keyword: String): List<FriendShip> {
        return friendShips.filter { friendShip ->
            friendShip.friendName.contains(keyword, ignoreCase = true)
        }
    }
}
