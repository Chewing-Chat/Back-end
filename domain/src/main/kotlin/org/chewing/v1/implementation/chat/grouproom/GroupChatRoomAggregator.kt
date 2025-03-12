package org.chewing.v1.implementation.chat.grouproom

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.chat.room.ThumbnailGroupChatRoom
import org.springframework.stereotype.Component

@Component
class GroupChatRoomAggregator {
    fun aggregatesThumbnail(
        chatRooms: List<GroupChatRoom>,
        chatLogs: List<ChatLog>,
    ): List<ThumbnailGroupChatRoom> {
        val chatRoomsById = chatRooms.associateBy { it.roomInfo.chatRoomId }
        return chatLogs.mapNotNull { chatMessage ->
            chatRoomsById[chatMessage.chatRoomId]?.let { chatRoom ->
                ThumbnailGroupChatRoom.of(
                    chatRoom,
                    chatMessage,
                )
            }
        }
    }

    fun aggregateThumbnail(
        chatRoom: GroupChatRoom,
        chatLog: ChatLog,
    ): ThumbnailGroupChatRoom {
        return ThumbnailGroupChatRoom.of(
            chatRoom,
            chatLog,
        )
    }
}
