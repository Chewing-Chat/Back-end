package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.ChatRoom

data class ChatRoomListResponse(
    val chatRooms: List<ChatRoomResponse>,
) {
    companion object {
        fun ofList(chatRooms: List<ChatRoom>): ChatRoomListResponse {
            return ChatRoomListResponse(
                chatRooms = chatRooms.map { ChatRoomResponse.of(it) },
            )
        }
    }
}
