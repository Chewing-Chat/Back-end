package org.chewing.v1.model.chat.room

class ChatRoomSequence private constructor(
    val sequence: Int,
    val chatRoomId: ChatRoomId,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            sequence: Int,
        ): ChatRoomSequence {
            return ChatRoomSequence(
                sequence = sequence,
                chatRoomId = chatRoomId,
            )
        }
    }
}
