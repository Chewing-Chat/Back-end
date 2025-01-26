package org.chewing.v1.model.chat.member

import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.ChatRoomMemberType
import org.chewing.v1.model.chat.room.ChatRoomId

class DirectChatRoomInfo private constructor(
    val chatRoomId: ChatRoomId,
    val type: ChatRoomMemberType,
    val status: ChatRoomMemberStatus
){
    companion object {
        fun of(
            chatRoomId: ChatRoomId,
            type: ChatRoomMemberType,
            status: ChatRoomMemberStatus
        ): DirectChatRoomInfo {
            return DirectChatRoomInfo(chatRoomId, type, status)
        }
    }
}
