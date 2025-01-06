package org.chewing.v1.model.chat.room

import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.member.ChatRoomMemberInfo

data class Room(
    val chatRoomId: String,
    val favorite: Boolean,
    val groupChatRoom: Boolean,
    val readSequenceNumber: Int,
    val startSequenceNumber: Int,
    val chatRoomMemberInfos: List<ChatRoomMember>,
) {
    companion object {
        fun of(
            chatRoomInfo: ChatRoomInfo,
            userChatRoom: ChatRoomMemberInfo,
            chatRoomMembers: List<ChatRoomMember>,
        ): Room {
            return Room(
                chatRoomId = chatRoomInfo.chatRoomId,
                favorite = userChatRoom.favorite,
                groupChatRoom = chatRoomInfo.isGroup,
                readSequenceNumber = userChatRoom.readSeqNumber,
                startSequenceNumber = userChatRoom.startSeqNumber,
                chatRoomMemberInfos = chatRoomMembers,
            )
        }
    }
}
