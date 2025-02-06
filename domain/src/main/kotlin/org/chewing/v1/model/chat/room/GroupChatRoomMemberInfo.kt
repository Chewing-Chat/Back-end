package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class GroupChatRoomMemberInfo private constructor(
    val chatRoomId: ChatRoomId,
    val memberId: UserId,
    val status: ChatRoomMemberStatus,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            memberId: UserId,
            status: ChatRoomMemberStatus,
        ): GroupChatRoomMemberInfo {
            return GroupChatRoomMemberInfo(chatRoomId, memberId, status)
        }
    }
}
