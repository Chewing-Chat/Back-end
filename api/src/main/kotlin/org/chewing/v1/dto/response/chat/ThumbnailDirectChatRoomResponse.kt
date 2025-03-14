package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.ThumbnailDirectChatRoom

data class ThumbnailDirectChatRoomResponse(
    val chatRoomId: String,
    val chatRoomSequenceNumber: Int,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val latestChatLog: ChatLogResponse,
    val chatRoomOwnStatus: String,
    val friendId: String,
) {
    companion object {
        fun of(thumbnailChatRoom: ThumbnailDirectChatRoom): ThumbnailDirectChatRoomResponse {
            return ThumbnailDirectChatRoomResponse(
                chatRoomId = thumbnailChatRoom.chatRoom.roomInfo.chatRoomId.id,
                readSequenceNumber = thumbnailChatRoom.chatRoom.ownSequence.readSequenceNumber,
                joinSequenceNumber = thumbnailChatRoom.chatRoom.ownSequence.joinSequenceNumber,
                chatRoomSequenceNumber = thumbnailChatRoom.chatRoom.roomSequence.sequence,
                latestChatLog = ChatLogResponse.from(thumbnailChatRoom.chatLog),
                chatRoomOwnStatus = thumbnailChatRoom.chatRoom.roomInfo.status.name.lowercase(),
                friendId = thumbnailChatRoom.chatRoom.roomInfo.friendId.id,
            )
        }
    }
}
