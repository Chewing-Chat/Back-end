package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.user.UserId

data class GroupChatRoomResponse(
    val chatRoomId: String,
    val chatRoomName: String,
    val chatRoomSequenceNumber: Int,
    val chatRoomOwnStatus: String,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val friendIds: List<String>,
) {
    companion object {
        fun of(
            groupChatRoom: GroupChatRoom,
            userId: UserId,
        ): GroupChatRoomResponse {
            val friendIds = groupChatRoom.memberInfos
                .filter { it.memberId != userId }
                .map { it.memberId.id }
            val chatRoomOwnStatus = groupChatRoom.memberInfos
                .find { it.memberId == userId }!!
            return GroupChatRoomResponse(
                chatRoomId = groupChatRoom.roomInfo.chatRoomId.id,
                chatRoomName = groupChatRoom.roomInfo.name,
                chatRoomSequenceNumber = groupChatRoom.roomSequence.sequenceNumber,
                chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                readSequenceNumber = groupChatRoom.ownSequence.readSequenceNumber,
                joinSequenceNumber = groupChatRoom.ownSequence.joinSequenceNumber,
                friendIds = friendIds,
            )
        }
    }
}
