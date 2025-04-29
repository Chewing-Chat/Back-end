package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class AiChatRoomInfo private constructor(
    val chatRoomId: ChatRoomId,
    val userId: UserId,
    val status: ChatRoomMemberStatus,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            userId: UserId,
            status: ChatRoomMemberStatus,
        ): AiChatRoomInfo {
            return AiChatRoomInfo(
                chatRoomId = chatRoomId,
                userId = userId,
                status = status,
            )
        }
    }
}
