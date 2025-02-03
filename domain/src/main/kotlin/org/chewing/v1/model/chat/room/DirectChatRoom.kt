package org.chewing.v1.model.chat.room

class DirectChatRoom private constructor(
    val chatRoomInfo: DirectChatRoomInfo,
    val chatRoomSequence: ChatSequence,
    val chatRoomMemberSequence: ChatSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: DirectChatRoomInfo,
            chatRoomSequence: ChatSequence,
            chatRoomMemberSequence: ChatSequence,
        ): DirectChatRoom {
            return DirectChatRoom(
                chatRoomInfo = chatRoomInfo,
                chatRoomSequence = chatRoomSequence,
                chatRoomMemberSequence = chatRoomMemberSequence,
            )
        }
    }
}
