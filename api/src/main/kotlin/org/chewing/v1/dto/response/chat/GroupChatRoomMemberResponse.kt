package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo

data class GroupChatRoomMemberResponse(
    val friendId: String,
    val friendStatus: String,
) {
    companion object {
        fun of(
            chatRoomMemberInfo: GroupChatRoomMemberInfo,
        ): GroupChatRoomMemberResponse {
            return GroupChatRoomMemberResponse(
                friendId = chatRoomMemberInfo.memberId.id,
                friendStatus = chatRoomMemberInfo.status.name.lowercase(),
            )
        }
    }
}
