package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class DirectChatRoomInfo private constructor(
    val directChatRoomId: DirectChatRoomId,
    val userId: UserId,
    val friendId: UserId,
){
    companion object {
        fun of(
            directChatRoomId: DirectChatRoomId,
            userId: UserId,
            friendId: UserId,
        ): DirectChatRoomInfo {
            return DirectChatRoomInfo(
                directChatRoomId = directChatRoomId,
                userId = userId,
                friendId = friendId,
            )
        }
    }
}
