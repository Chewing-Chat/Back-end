package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class DirectChatRoomInfo private constructor(
    val chatRoomId: ChatRoomId,
    val userId: UserId,
    val friendId: UserId,
){
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            userId: UserId,
            friendId: UserId,
        ): DirectChatRoomInfo {
            return DirectChatRoomInfo(
                chatRoomId = chatRoomId,
                userId = userId,
                friendId = friendId,
            )
        }
    }
}
