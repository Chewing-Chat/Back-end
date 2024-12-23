package org.chewing.v1.model.chat.room

class ChatNumber private constructor(
    val sequenceNumber: Int,
    val chatRoomId: String,
    val page: Int,
) {
    companion object {
        fun of(
            chatRoomId: String,
            sequenceNumber: Int,
            page: Int,
        ): ChatNumber {
            return ChatNumber(
                sequenceNumber = sequenceNumber,
                chatRoomId = chatRoomId,
                page = page,
            )
        }
    }
}
