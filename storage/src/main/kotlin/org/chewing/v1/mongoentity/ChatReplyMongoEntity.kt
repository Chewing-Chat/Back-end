package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.message.ChatReplyMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatReplyMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    sendTime: LocalDateTime,
    private val message: String,
    private val parentMessageId: String,
    private val parentSeqNumber: Int,
    private val parentMessageText: String,
    private val parentMessageType: ChatLogType,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    type = ChatLogType.REPLY,
    senderId = senderId,
    seqNumber = seqNumber,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatReplyMessage: ChatReplyMessage,
        ): ChatReplyMongoEntity {
            return ChatReplyMongoEntity(
                messageId = chatReplyMessage.messageId,
                chatRoomId = chatReplyMessage.chatRoomId.id,
                senderId = chatReplyMessage.senderId.id,
                seqNumber = chatReplyMessage.number.sequenceNumber,
                sendTime = chatReplyMessage.timestamp,
                message = chatReplyMessage.text,
                parentMessageId = chatReplyMessage.parentMessageId,
                parentSeqNumber = chatReplyMessage.parentSeqNumber,
                parentMessageText = chatReplyMessage.parentMessageText,
                parentMessageType = chatReplyMessage.parentMessageType,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatReplyLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            parentMessageId = parentMessageId,
            parentSeqNumber = parentSeqNumber,
            parentMessageText = parentMessageText,
            text = message,
            timestamp = sendTime,
            type = type,
            number = ChatSequence.of(ChatRoomId.of(chatRoomId), seqNumber),
            parentMessageType = parentMessageType,
        )
    }
}
