package org.chewing.v1.implementation.search

import org.chewing.v1.model.chat.room.Room
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class ChatRoomSearchEngine {

    fun search(friendIds: List<UserId>, rooms: List<Room>): List<Room> {
        val friendIdSet = friendIds.map { it.id }.toSet()
        return rooms.filter { it.hasMemberInFriendIds(friendIdSet) }
    }

    private fun Room.hasMemberInFriendIds(friendIds: Set<String>): Boolean {
        return this.chatRoomMemberInfos.any { member -> member.memberId.id in friendIds }
    }
}
