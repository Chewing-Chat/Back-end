package org.chewing.v1.model.chat.room

class ChatSequence private constructor(
    val sequenceNumber: Int,
    val chatRoomId: ChatRoomId,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            sequenceNumber: Int,
        ): ChatSequence {
            return ChatSequence(
                sequenceNumber = sequenceNumber,
                chatRoomId = chatRoomId,
            )
        }
    }
}
