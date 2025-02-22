package org.chewing.v1.model.chat.room

class GroupChatRoom private constructor(
    val roomInfo: GroupChatRoomInfo,
    val memberInfos: List<GroupChatRoomMemberInfo>,
    val roomSequence: ChatRoomSequence,
    val ownSequence: ChatRoomMemberSequence,
) {
    companion object {
        fun of(
            chatRoomInfo: GroupChatRoomInfo,
            chatRoomMembers: List<GroupChatRoomMemberInfo>,
            chatRoomSequence: ChatRoomSequence,
            chatRoomMemberSequence: ChatRoomMemberSequence,
        ): GroupChatRoom {
            return GroupChatRoom(
                roomInfo = chatRoomInfo,
                roomSequence = chatRoomSequence,
                ownSequence = chatRoomMemberSequence,
                memberInfos = chatRoomMembers,
            )
        }
    }
}
