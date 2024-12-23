package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatBombLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatBombMessage
import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatBombMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    page: Int,
    sendTime: LocalDateTime,
    private val message: String,
    private val expiredAt: LocalDateTime,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.BOMB,
    seqNumber = seqNumber,
    page = page,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatBombMessage: ChatBombMessage,
        ): ChatBombMongoEntity {
            return ChatBombMongoEntity(
                messageId = chatBombMessage.messageId,
                chatRoomId = chatBombMessage.chatRoomId,
                senderId = chatBombMessage.senderId,
                seqNumber = chatBombMessage.number.sequenceNumber,
                page = chatBombMessage.number.page,
                sendTime = chatBombMessage.timestamp,
                message = chatBombMessage.text,
                expiredAt = chatBombMessage.expiredAt,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatBombLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = senderId,
            timestamp = sendTime,
            number = ChatNumber.of(chatRoomId, seqNumber, page),
            text = message,
            type = type,
            expiredAt = expiredAt,
        )
    }
}
