package org.chewing.v1.model.chat.room

import org.chewing.v1.model.chat.log.*
import org.chewing.v1.model.chat.member.ChatRoomMember
import org.chewing.v1.model.chat.message.*
import java.time.LocalDateTime

data class ChatRoom(
    val chatRoomId: String,
    val favorite: Boolean,
    val groupChatRoom: Boolean,
    val latestMessage: String,
    val latestMessageTime: LocalDateTime,
    val totalUnReadMessage: Int,
    val latestSeqNumber: Int,
    val latestPage: Int,
    val chatRoomMemberInfos: List<ChatRoomMember>,
) {
    companion object {
        fun of(
            room: Room,
            chatLog: ChatLog,
        ): ChatRoom {
            val totalUnReadMessage = chatLog.number.sequenceNumber - room.readSequenceNumber
            when (chatLog) {
                is ChatNormalLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = chatLog.text,
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }

                is ChatReplyLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = chatLog.text,
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }

                is ChatFileLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = chatLog.medias[0].url,
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }

                is ChatInviteLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = "",
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }

                is ChatLeaveLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = "",
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }

                is ChatBombLog -> {
                    return ChatRoom(
                        chatRoomId = room.chatRoomId,
                        favorite = room.favorite,
                        groupChatRoom = room.groupChatRoom,
                        latestMessage = chatLog.text,
                        latestMessageTime = chatLog.timestamp,
                        totalUnReadMessage = totalUnReadMessage,
                        latestSeqNumber = chatLog.number.sequenceNumber,
                        latestPage = chatLog.number.page,
                        chatRoomMemberInfos = room.chatRoomMemberInfos,
                    )
                }
            }
        }
    }
}
