package org.chewing.v1.implementation.chat.room

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.Room
import org.springframework.stereotype.Component

@Component
class ChatRoomAggregator {
    fun aggregateChatRoom(
        chatRooms: List<Room>,
        latestMessages: List<ChatLog>,
    ): List<ChatRoom> {
        val chatRoomMap = chatRooms.associateBy { it.chatRoomId }
        return latestMessages.mapNotNull {
            val myChatRoom = chatRoomMap[it.chatRoomId]
                ?: return@mapNotNull null
            ChatRoom.of(
                room = myChatRoom,
                chatLog = it,
            )
        }
    }
}
