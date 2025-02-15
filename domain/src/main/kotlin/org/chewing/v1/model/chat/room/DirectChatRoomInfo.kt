package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class DirectChatRoomInfo private constructor(
    val chatRoomId: ChatRoomId,
    val userId: UserId,
    val friendId: UserId,
    val status: ChatRoomMemberStatus,
    val friendStatus: ChatRoomMemberStatus,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            userId: UserId,
            friendId: UserId,
            status: ChatRoomMemberStatus,
            friendStatus: ChatRoomMemberStatus,
        ): DirectChatRoomInfo {
            return DirectChatRoomInfo(
                chatRoomId = chatRoomId,
                userId = userId,
                friendId = friendId,
                status = status,
                friendStatus = friendStatus,
            )
        }
    }
}
