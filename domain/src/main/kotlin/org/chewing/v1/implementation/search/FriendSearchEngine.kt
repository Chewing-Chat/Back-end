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
            val fullName = "${friendShip.friendName.firstName} ${friendShip.friendName.lastName()}"
            val alternativeFullName = "${friendShip.friendName.lastName()} ${friendShip.friendName.firstName()}"
            val concatenatedNames = "${friendShip.friendName.firstName()}${friendShip.friendName.lastName()}"

            listOf(fullName, alternativeFullName, concatenatedNames).any {
                it.contains(keyword, ignoreCase = true)
            }
        }
    }
}
