package org.chewing.v1.model.chat.room

class DirectChatRoom private constructor(
    val chatRoomInfo: DirectChatRoomInfo,
    val chatRoomMemberInfo: DirectChatRoomMemberInfo,
    val chatRoomSequence: DirectChatLogSequence,
    val chatRoomMemberSequence: DirectChatLogSequence
){
    companion object {
        fun of(
            chatRoomInfo: DirectChatRoomInfo,
            chatRoomMemberInfo: DirectChatRoomMemberInfo,
            chatRoomSequence: DirectChatLogSequence,
            chatRoomMemberSequence: DirectChatLogSequence,
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
