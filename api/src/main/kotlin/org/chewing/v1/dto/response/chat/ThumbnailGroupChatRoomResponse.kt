package org.chewing.v1.dto.response.chat

import org.chewing.v1.model.chat.log.ChatCommentLog
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.room.ThumbnailGroupChatRoom
import org.chewing.v1.model.user.UserId

data class ThumbnailGroupChatRoomResponse(
    val chatRoomId: String,
    val chatRoomName: String,
    val readSequenceNumber: Int,
    val joinSequenceNumber: Int,
    val chatRoomSequenceNumber: Int,
    val latestChatLog: ChatLogResponse,
    val chatRoomOwnStatus: String,
    val friendInfos: List<GroupChatRoomMemberResponse>,
) {
    companion object {
        fun of(thumbnailChatRoom: ThumbnailGroupChatRoom, userId: UserId): ThumbnailGroupChatRoomResponse {
            val friendInfos = thumbnailChatRoom.chatRoom.memberInfos
                .filter { it.memberId != userId }
            val chatRoomOwnStatus = thumbnailChatRoom.chatRoom.memberInfos
                .find { it.memberId == userId }!!
            val chatLog = thumbnailChatRoom.chatLog
            val chatRoom = thumbnailChatRoom.chatRoom
            return when (chatLog) {
                is ChatReplyLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = thumbnailChatRoom.chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,

                )

                is ChatFileLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,

                )
                is ChatInviteLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,

                )
                is ChatLeaveLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,

                )
                is ChatNormalLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,
                )

                is ChatCommentLog -> ThumbnailGroupChatRoomResponse(
                    chatRoomId = chatRoom.roomInfo.chatRoomId.id,
                    chatRoomOwnStatus = chatRoomOwnStatus.status.name.lowercase(),
                    latestChatLog = ChatLogResponse.from(chatLog),
                    readSequenceNumber = chatRoom.ownSequence.readSequenceNumber,
                    joinSequenceNumber = chatRoom.ownSequence.joinSequenceNumber,
                    friendInfos = friendInfos.map { GroupChatRoomMemberResponse.of(it) },
                    chatRoomSequenceNumber = chatRoom.roomSequence.sequence,
                    chatRoomName = chatRoom.roomInfo.name,
                )
            }
        }
    }
}
