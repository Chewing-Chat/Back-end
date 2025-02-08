package org.chewing.v1.model.chat.room

class DirectChatRoom private constructor(
    val chatRoomInfo: DirectChatRoomInfo,
    val chatRoomSequence: ChatRoomSequence,
    val chatRoomOwnSequence: ChatRoomMemberSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: DirectChatRoomInfo,
            chatRoomSequence: ChatRoomSequence,
            chatRoomMemberSequence: ChatRoomMemberSequence,
        ): DirectChatRoom {
            return DirectChatRoom(
                chatRoomInfo = chatRoomInfo,
                chatRoomSequence = chatRoomSequence,
                chatRoomOwnSequence = chatRoomMemberSequence,
            )
        }
    }
}
