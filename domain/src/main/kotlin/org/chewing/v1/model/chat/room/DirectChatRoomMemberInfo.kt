package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class DirectChatRoomMemberInfo private constructor(
    val id: ChatRoomId,
    val memberId: UserId,
    val type: ChatRoomMemberType,
    val status: ChatRoomMemberStatus,
) {
    companion object {
        fun of(
            id: ChatRoomId,
            memberId: UserId,
            type: ChatRoomMemberType,
            status: ChatRoomMemberStatus,
        ): DirectChatRoomMemberInfo {
            return DirectChatRoomMemberInfo(id, memberId, type, status)
        }
    }
}
