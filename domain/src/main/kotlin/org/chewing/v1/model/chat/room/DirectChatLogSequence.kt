package org.chewing.v1.model.chat.room


class DirectChatLogSequence private constructor(
    val sequenceNumber: Int,
    val chatRoomId: ChatRoomId,
    val page: Int,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            sequenceNumber: Int,
            page: Int,
        ): DirectChatLogSequence {
            return DirectChatLogSequence(
                sequenceNumber = sequenceNumber,
                chatRoomId = chatRoomId,
                page = page,
            )
        }
    }
}
