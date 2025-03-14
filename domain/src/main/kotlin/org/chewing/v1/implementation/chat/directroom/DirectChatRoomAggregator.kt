package org.chewing.v1.implementation.chat.directroom

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.ThumbnailDirectChatRoom
import org.springframework.stereotype.Component

@Component
class DirectChatRoomAggregator {
    fun aggregatesThumbnail(
        directChatRooms: List<DirectChatRoom>,
        chatLogs: List<ChatLog>,
    ): List<ThumbnailDirectChatRoom> {
        val chatRoomsById = directChatRooms.associateBy { it.roomInfo.chatRoomId }
        return chatLogs.mapNotNull { chatMessage ->
            chatRoomsById[chatMessage.chatRoomId]?.let { chatRoom ->
                ThumbnailDirectChatRoom.of(
                    chatRoom,
                    chatMessage,
                )
            }
        }
    }

    fun aggregateThumbnail(
        directChatRoom: DirectChatRoom,
        chatLog: ChatLog,
    ): ThumbnailDirectChatRoom {
        return ThumbnailDirectChatRoom.of(
            directChatRoom,
            chatLog,
        )
    }
}
