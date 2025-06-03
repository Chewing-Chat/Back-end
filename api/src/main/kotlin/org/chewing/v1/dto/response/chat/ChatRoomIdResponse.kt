package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.ChatRoomId

class ChatRoomIdResponse(
    val chatRoomId: String,
) {
    companion object {
        fun of(chatRoomId: ChatRoomId): ChatRoomIdResponse {
            return ChatRoomIdResponse(chatRoomId.id)
        }
    }
}
