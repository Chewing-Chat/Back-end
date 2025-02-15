package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.DirectChatRoom
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.user.UserId

data class ChatRoomListResponse(
    val directChatRooms: List<FullDirectChatRoomResponse>,
    val groupChatRooms: List<FullGroupChatRoomResponse>,
) {
    companion object {
        fun from(
            directChats: List<Pair<DirectChatRoom, ChatLog>>,
            groupChats: List<Pair<GroupChatRoom, ChatLog>>,
            userId: UserId,
        ): ChatRoomListResponse {
            val directChatResponses = directChats.map { (chatRoom, chatLog) ->
                FullDirectChatRoomResponse.of(chatRoom, chatLog)
            }

            val groupChatResponses = groupChats.map { (chatRoom, chatLog) ->
                FullGroupChatRoomResponse.of(chatRoom, chatLog, userId)
            }

            return ChatRoomListResponse(directChatResponses, groupChatResponses)
        }
    }
}
