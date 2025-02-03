package org.chewing.v1.model.chat.room

class ChatSequenceNumber private constructor(
    val sequenceNumber: Int,
    val chatRoomId: String,
) {
    companion object {
        fun of(sequenceNumber: Int, chatRoomId: String): ChatSequenceNumber {
            return ChatSequenceNumber(sequenceNumber, chatRoomId)
        }
    }
}
