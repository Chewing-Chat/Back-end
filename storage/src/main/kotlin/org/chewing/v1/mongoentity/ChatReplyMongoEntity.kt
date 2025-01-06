package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.message.ChatReplyMessage
import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatReplyMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    page: Int,
    sendTime: LocalDateTime,
    private val message: String,
    private val parentMessageId: String,
    private val parentMessagePage: Int,
    private val parentSeqNumber: Int,
    private val parentMessageText: String,
    private val parentMessageType: ChatLogType,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    seqNumber = seqNumber,
    page = page,
    sendTime = sendTime,
    type = ChatLogType.REPLY,
) {
    companion object {
        fun from(
            chatReplyMessage: ChatReplyMessage,
        ): ChatReplyMongoEntity {
            return ChatReplyMongoEntity(
                messageId = chatReplyMessage.messageId,
                chatRoomId = chatReplyMessage.chatRoomId,
                senderId = chatReplyMessage.senderId,
                seqNumber = chatReplyMessage.number.sequenceNumber,
                page = chatReplyMessage.number.page,
                sendTime = chatReplyMessage.timestamp,
                message = chatReplyMessage.text,
                parentMessageId = chatReplyMessage.parentMessageId,
                parentMessagePage = chatReplyMessage.parentMessagePage,
                parentSeqNumber = chatReplyMessage.parentSeqNumber,
                parentMessageText = chatReplyMessage.parentMessageText,
                parentMessageType = chatReplyMessage.parentMessageType,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatReplyLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = senderId,
            parentMessageId = parentMessageId,
            parentMessagePage = parentMessagePage,
            parentSeqNumber = parentSeqNumber,
            parentMessageText = parentMessageText,
            text = message,
            timestamp = sendTime,
            type = type,
            number = ChatNumber.of(chatRoomId, seqNumber, page),
            parentMessageType = parentMessageType,
        )
    }
}
