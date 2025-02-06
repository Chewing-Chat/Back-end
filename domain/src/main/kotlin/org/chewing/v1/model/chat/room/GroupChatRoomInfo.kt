package org.chewing.v1.model.chat.room

import org.chewing.v1.model.user.UserId

class GroupChatRoomInfo private constructor(
    val chatRoomId: ChatRoomId,
    val name: String,
) {
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            name: String,
        ): GroupChatRoomInfo {
            return GroupChatRoomInfo(
                chatRoomId = chatRoomId,
                name = name,
            )
        }
    }
}
