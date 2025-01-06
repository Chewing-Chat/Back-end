package org.chewing.v1.model.chat.message

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

class ChatReplyMessage private constructor(
    val messageId: String,
    override val chatRoomId: String,
    override val senderId: String,
    override val timestamp: LocalDateTime,
    override val number: ChatNumber,
    override val type: MessageType,
    val text: String,
    val parentMessageId: String,
    val parentMessagePage: Int,
    val parentMessageText: String,
    val parentSeqNumber: Int,
    val parentMessageType: ChatLogType,
) : ChatMessage() {
    companion object {
        fun of(
            messageId: String,
            chatRoomId: String,
            senderId: String,
            parentMessageId: String,
            parentMessagePage: Int,
            parentSeqNumber: Int,
            timestamp: LocalDateTime,
            number: ChatNumber,
            text: String,
            parentMessageText: String,
            type: MessageType,
            parentMessageType: ChatLogType,
        ): ChatReplyMessage {
            return ChatReplyMessage(
                messageId = messageId,
                chatRoomId = chatRoomId,
                senderId = senderId,
                parentMessageId = parentMessageId,
                parentMessagePage = parentMessagePage,
                parentSeqNumber = parentSeqNumber,
                timestamp = timestamp,
                number = number,
                text = text,
                parentMessageText = parentMessageText,
                type = type,
                parentMessageType = parentMessageType,
            )
        }
    }
}
