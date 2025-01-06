package org.chewing.v1.dto.response.chat

import org.chewing.v1.dto.response.media.MediaResponse
import org.chewing.v1.model.chat.log.*
import java.time.format.DateTimeFormatter

sealed class ChatLogMessageResponse {
    data class Reply(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val parentMessageId: String,
        val parentMessagePage: Int,
        val parentSeqNumber: Int,
        val parentMessageText: String,
        val parentMessageType: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val text: String,
    ) : ChatLogMessageResponse()

    data class Leave(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
    ) : ChatLogMessageResponse()

    data class Invite(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val targetUserIds: List<String>,
    ) : ChatLogMessageResponse()

    data class File(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val files: List<MediaResponse>,
    ) : ChatLogMessageResponse()

    data class Normal(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val text: String,
    ) : ChatLogMessageResponse()

    data class Bomb(
        val messageId: String,
        val type: String,
        val chatRoomId: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val page: Int,
        val expiredAt: String,
        val text: String,
    ) : ChatLogMessageResponse()

    companion object {
        fun from(chatLog: ChatLog): ChatLogMessageResponse {
            val formattedTime = chatLog.timestamp.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))

            return when (chatLog) {
                is ChatReplyLog -> Reply(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    chatRoomId = chatLog.chatRoomId,
                    senderId = chatLog.senderId,
                    parentMessageId = chatLog.parentMessageId,
                    parentMessagePage = chatLog.parentMessagePage,
                    parentSeqNumber = chatLog.parentSeqNumber,
                    parentMessageText = chatLog.parentMessageText,
                    parentMessageType = chatLog.parentMessageType.toString().lowercase(),
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    page = chatLog.number.page,
                    text = chatLog.text,
                )

                is ChatLeaveLog -> Leave(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    chatRoomId = chatLog.chatRoomId,
                    senderId = chatLog.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    page = chatLog.number.page,
                )

                is ChatInviteLog -> Invite(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    chatRoomId = chatLog.chatRoomId,
                    senderId = chatLog.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    page = chatLog.number.page,
                    targetUserIds = chatLog.targetUserIds,
                )

                is ChatFileLog -> File(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    chatRoomId = chatLog.chatRoomId,
                    senderId = chatLog.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    page = chatLog.number.page,
                    files = chatLog.medias.map { MediaResponse.from(it) },
                )

                is ChatNormalLog -> Normal(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    chatRoomId = chatLog.chatRoomId,
                    senderId = chatLog.senderId,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    page = chatLog.number.page,
                    text = chatLog.text,
                )

                is ChatBombLog ->

                    Bomb(
                        messageId = chatLog.messageId,
                        type = chatLog.type.name.lowercase(),
                        chatRoomId = chatLog.chatRoomId,
                        senderId = chatLog.senderId,
                        timestamp = formattedTime,
                        seqNumber = chatLog.number.sequenceNumber,
                        page = chatLog.number.page,
                        expiredAt = chatLog.expiredAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")),
                        text = chatLog.text,
                    )
            }
        }
    }
}
