package org.chewing.v1.dto.response.chat

import org.chewing.v1.dto.response.media.MediaResponse
import org.chewing.v1.model.chat.log.*
import java.time.format.DateTimeFormatter

sealed class ChatLogResponse {
    data class Reply(
        val messageId: String,
        val type: String,
        val senderId: String,
        val parentMessageId: String,
        val parentSeqNumber: Int,
        val parentMessageText: String,
        val parentMessageType: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
    ) : ChatLogResponse()

    data class Leave(
        val messageId: String,
        val type: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
    ) : ChatLogResponse()

    data class Invite(
        val messageId: String,
        val type: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val targetUserIds: List<String>,
    ) : ChatLogResponse()

    data class File(
        val messageId: String,
        val type: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val files: List<MediaResponse>,
    ) : ChatLogResponse()

    data class Normal(
        val messageId: String,
        val type: String,
        val senderId: String,
        val timestamp: String,
        val seqNumber: Int,
        val text: String,
    ) : ChatLogResponse()

    companion object {
        fun from(chatLog: ChatLog): ChatLogResponse {
            val formattedTime = chatLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))

            return when (chatLog) {
                is ChatReplyLog -> Reply(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    senderId = chatLog.senderId.id,
                    parentMessageId = chatLog.parentMessageId,
                    parentSeqNumber = chatLog.parentSeqNumber,
                    parentMessageText = chatLog.parentMessageText,
                    parentMessageType = chatLog.parentMessageType.toString().lowercase(),
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    text = chatLog.text,
                )

                is ChatLeaveLog -> Leave(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    senderId = chatLog.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                )

                is ChatInviteLog -> Invite(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    senderId = chatLog.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    targetUserIds = chatLog.targetUserIds.map { it.id },
                )

                is ChatFileLog -> File(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    senderId = chatLog.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    files = chatLog.medias.map { MediaResponse.from(it) },
                )

                is ChatNormalLog -> Normal(
                    messageId = chatLog.messageId,
                    type = chatLog.type.name.lowercase(),
                    senderId = chatLog.senderId.id,
                    timestamp = formattedTime,
                    seqNumber = chatLog.number.sequenceNumber,
                    text = chatLog.text,
                )
            }
        }
    }
}
