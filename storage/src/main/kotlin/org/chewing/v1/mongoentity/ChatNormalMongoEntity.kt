package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.message.ChatNormalMessage
import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatNormalMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    page: Int,
    sendTime: LocalDateTime,
    private val message: String,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.NORMAL,
    seqNumber = seqNumber,
    page = page,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatNormalMessage: ChatNormalMessage,
        ): ChatNormalMongoEntity {
            return ChatNormalMongoEntity(
                messageId = chatNormalMessage.messageId,
                chatRoomId = chatNormalMessage.chatRoomId,
                senderId = chatNormalMessage.senderId,
                seqNumber = chatNormalMessage.number.sequenceNumber,
                page = chatNormalMessage.number.page,
                sendTime = chatNormalMessage.timestamp,
                message = chatNormalMessage.text,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatNormalLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = senderId,
            timestamp = sendTime,
            number = ChatNumber.of(chatRoomId, seqNumber, page),
            text = message,
            type = type,
        )
    }
}
