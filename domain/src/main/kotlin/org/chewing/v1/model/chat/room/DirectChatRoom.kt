package org.chewing.v1.model.chat.room

class DirectChatRoom private constructor(
    val roomInfo: DirectChatRoomInfo,
    val roomSequence: ChatRoomSequence,
    val ownSequence: ChatRoomMemberSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: DirectChatRoomInfo,
            chatRoomSequence: ChatRoomSequence,
            chatRoomMemberSequence: ChatRoomMemberSequence,
        ): DirectChatRoom {
            return DirectChatRoom(
                roomInfo = chatRoomInfo,
                roomSequence = chatRoomSequence,
                ownSequence = chatRoomMemberSequence,
            )
        }
    }
}
