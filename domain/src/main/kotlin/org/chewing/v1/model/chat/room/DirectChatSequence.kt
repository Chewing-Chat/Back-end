package org.chewing.v1.model.chat.room


class DirectChatSequence private constructor(
    val sequenceNumber: Int,
    val chatRoomId: ChatRoomId,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            sequenceNumber: Int,
        ): DirectChatSequence {
            return DirectChatSequence(
                sequenceNumber = sequenceNumber,
                chatRoomId = chatRoomId,
            )
        }
    }
}
