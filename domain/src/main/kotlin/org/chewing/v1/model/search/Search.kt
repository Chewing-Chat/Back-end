package org.chewing.v1.model.search

import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.friend.FriendShip

class Search private constructor(
    val chatRooms: List<ChatRoom>,
    val friends: List<FriendShip>,
) {
    companion object {
        fun of(chatRooms: List<ChatRoom>, friends: List<FriendShip>): Search {
            return Search(chatRooms, friends)
        }
    }
}
