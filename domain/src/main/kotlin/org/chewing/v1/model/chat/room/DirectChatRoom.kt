package org.chewing.v1.model.chat.room

class DirectChatRoom private constructor(
    val chatRoomInfo: DirectChatRoomInfo,
    val chatRoomMemberInfo: DirectChatRoomMemberInfo,
    val chatRoomSequence: DirectChatSequence,
    val chatRoomMemberSequence: DirectChatSequence
){
    companion object {
        fun of(
            chatRoomInfo: DirectChatRoomInfo,
            chatRoomMemberInfo: DirectChatRoomMemberInfo,
            chatRoomSequence: DirectChatSequence,
            chatRoomMemberSequence: DirectChatSequence,
        ): DirectChatRoom {
            return DirectChatRoom(
                chatRoomInfo = chatRoomInfo,
                chatRoomMemberInfo = chatRoomMemberInfo,
                chatRoomSequence = chatRoomSequence,
                chatRoomMemberSequence = chatRoomMemberSequence,
            )
        }
    }
}
