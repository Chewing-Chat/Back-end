package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.DirectChatRoom

data class DirectChatRoomResponse(
    val chatRoomId: String,
    val chatRoomSequenceNumber: Int,
    val chatRoomOwnStatus: String,
    val friendId: String,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
) {
    companion object {
        fun of(
            directChatRoom: DirectChatRoom,
        ): DirectChatRoomResponse {
            return DirectChatRoomResponse(
                chatRoomId = directChatRoom.roomInfo.chatRoomId.id,
                chatRoomSequenceNumber = directChatRoom.roomSequence.sequenceNumber,
                chatRoomOwnStatus = directChatRoom.roomInfo.status.name.lowercase(),
                friendId = directChatRoom.roomInfo.friendId.id,
                readSequenceNumber = directChatRoom.ownSequence.readSequenceNumber,
                joinSequenceNumber = directChatRoom.ownSequence.joinSequenceNumber,
            )
        }
    }
}
