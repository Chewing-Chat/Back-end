package org.chewing.v1.model.chat.room

class GroupChatRoom private constructor(
    val chatRoomInfo: GroupChatRoomInfo,
    val chatRoomMembers : List<GroupChatRoomMemberInfo>,
    val chatRoomSequence: ChatSequence,
    val chatRoomOwnSequence: ChatSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: GroupChatRoomInfo,
            chatRoomMembers : List<GroupChatRoomMemberInfo>,
            chatRoomSequence: ChatSequence,
            chatRoomMemberSequence: ChatSequence,
        ): GroupChatRoom {
            return GroupChatRoom(
                chatRoomInfo = chatRoomInfo,
                chatRoomSequence = chatRoomSequence,
                chatRoomOwnSequence = chatRoomMemberSequence,
                chatRoomMembers = chatRoomMembers,

            )
        }
    }
}

