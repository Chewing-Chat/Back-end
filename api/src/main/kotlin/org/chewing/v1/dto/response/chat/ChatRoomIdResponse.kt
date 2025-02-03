package org.chewing.v1.dto.response.chat

class ChatRoomIdResponse(
    val chatRoomId: String,
) {
    companion object {
        fun from(chatRoomId: String): ChatRoomIdResponse {
            return ChatRoomIdResponse(chatRoomId)
        }
    }
}
