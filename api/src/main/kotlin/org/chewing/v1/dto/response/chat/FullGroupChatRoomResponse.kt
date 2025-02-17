package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.room.GroupChatRoom
import org.chewing.v1.model.user.UserId

data class FullGroupChatRoomResponse(
    val chatRoomId: String,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val chatRoomSequenceNumber: Int,
    val latestChatLog: ChatLogResponse,
    val chatRoomOwnStatus: String,
    val friendIds: List<String>,
) {
    companion object {
        fun of(chatRoom: GroupChatRoom, chatLog: ChatLog, userId: UserId): FullGroupChatRoomResponse {
            val friendIds = chatRoom.memberInfos
                .filter { it.memberId != userId }
                .map { it.memberId.id }
            val chatRoomOwnStatus = chatRoom.memberInfos
                .find { it.memberId == userId }!!
            return when (chatLog) {
                is ChatReplyLog -> FullGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendIds = friendIds,
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,

                    )

                is ChatFileLog -> FullGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendIds = friendIds,
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,

                    )
                is ChatInviteLog -> FullGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendIds = friendIds,
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,

                    )
                is ChatLeaveLog -> FullGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendIds = friendIds,
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,

                    )
                is ChatNormalLog -> FullGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendIds = friendIds,
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequenceNumber,

                    )
            }
        }
    }
}
