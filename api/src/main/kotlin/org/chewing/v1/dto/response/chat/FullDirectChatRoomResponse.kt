package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.DirectChatRoom

data class FullDirectChatRoomResponse(
    val chatRoomId: String,
    val chatRoomSequenceNumber: Int,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val latestChatLog: ChatLogResponse,
    val chatRoomMemberStatus: String,
    val friendId: String,
) {
    companion object {
        fun of(chatRoom: DirectChatRoom, chatLog: ChatLog): FullDirectChatRoomResponse {
            return FullDirectChatRoomResponse(
                chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,
                latestChatLog = ChatLogResponse.from(chatLog),
                chatRoomMemberStatus = chatRoom.roomInfo.status.name.lowercase(),
                friendId = chatRoom.roomInfo.friendId.id,
            )
        }
    }
}
