package org.chewing.v1.implementation.search

import org.chewing.v1.model.chat.room.Room
import org.springframework.stereotype.Component

@Component
class ChatRoomSearchEngine {

    fun search(friendIds: List<String>, rooms: List<Room>): List<Room> {
        val friendIdSet = friendIds.toSet()
        return rooms.filter { room -> room.hasMemberInFriendIds(friendIdSet) }
    }

    private fun Room.hasMemberInFriendIds(friendIds: Set<String>): Boolean {
        return this.chatRoomMemberInfos.any { member -> friendIds.contains(member.memberId) }
    }
}
