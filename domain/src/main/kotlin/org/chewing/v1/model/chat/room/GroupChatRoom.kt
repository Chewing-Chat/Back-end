package org.chewing.v1.model.chat.room

class GroupChatRoom private constructor(
    val chatRoomInfo: GroupChatRoomInfo,
    val chatRoomMembers : List<GroupChatRoomMemberInfo>,
    val chatRoomSequence: ChatRoomSequence,
    val chatRoomOwnSequence: ChatRoomMemberSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: GroupChatRoomInfo,
            chatRoomMembers : List<GroupChatRoomMemberInfo>,
            chatRoomSequence: ChatRoomSequence,
            chatRoomMemberSequence: ChatRoomMemberSequence,
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

