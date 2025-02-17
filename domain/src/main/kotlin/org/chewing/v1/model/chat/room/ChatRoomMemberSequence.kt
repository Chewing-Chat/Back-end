package org.chewing.v1.model.chat.room

class ChatRoomMemberSequence private constructor(
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val chatRoomId: ChatRoomId,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            readSequenceNumber: Int,
            joinSequenceNumber: Int,
        ): ChatRoomMemberSequence {
            return ChatRoomMemberSequence(
                readSequenceNumber = readSequenceNumber,
                joinSequenceNumber = joinSequenceNumber,
                chatRoomId = chatRoomId,
            )
        }
    }
}
