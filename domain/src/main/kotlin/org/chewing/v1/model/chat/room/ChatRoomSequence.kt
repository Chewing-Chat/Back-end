package org.chewing.v1.model.chat.room

class ChatRoomSequence private constructor(
    val sequenceNumber: Int,
    val chatRoomId: ChatRoomId,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            sequenceNumber: Int,
        ): ChatRoomSequence {
            return ChatRoomSequence(
                sequenceNumber = sequenceNumber,
                chatRoomId = chatRoomId,
            )
        }
    }
}
