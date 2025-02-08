package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.message.ChatNormalMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatNormalMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    sendTime: LocalDateTime,
    private val message: String,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    type = ChatLogType.NORMAL,
    senderId = senderId,
    seqNumber = seqNumber,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatNormalMessage: ChatNormalMessage,
        ): ChatNormalMongoEntity {
            return ChatNormalMongoEntity(
                messageId = chatNormalMessage.messageId,
                chatRoomId = chatNormalMessage.chatRoomId.id,
                senderId = chatNormalMessage.senderId.id,
                seqNumber = chatNormalMessage.number.sequenceNumber,
                sendTime = chatNormalMessage.timestamp,
                message = chatNormalMessage.text,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatNormalLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = sendTime,
            number = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), seqNumber),
            text = message,
            type = type,
        )
    }
}
