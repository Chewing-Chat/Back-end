package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomSortCriteria
import org.springframework.stereotype.Component

@Component
object ChatRoomSortEngine {
    fun sortChatRoom(
        chatRooms: List<ChatRoom>,
        sort: ChatRoomSortCriteria,
    ): List<ChatRoom> {
        return when (sort) {
            ChatRoomSortCriteria.FAVORITE -> chatRooms.sortedWith(
                compareByDescending<ChatRoom> { it.favorite }.thenByDescending { it.latestMessageTime },
            )
            ChatRoomSortCriteria.DATE -> chatRooms.sortedWith(
                compareByDescending<ChatRoom> { it.latestMessageTime },
            )
            ChatRoomSortCriteria.NOT_READ -> chatRooms.sortedWith(
                compareByDescending<ChatRoom> { it.totalUnReadMessage != 0 }.thenByDescending { it.latestMessageTime },
            )
        }
    }
}
