package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatAiLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.ChatAiMessage

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatAiMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    sequence: Int,
    createAt: LocalDateTime,
    private val message: String,
    private val senderType: SenderType,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    type = ChatLogType.NORMAL,
    senderId = senderId,
    sequence = sequence,
    createAt = createAt,
) {
    companion object {
        fun from(
            chatAiMessage: ChatAiMessage,
        ): ChatAiMongoEntity {
            return ChatAiMongoEntity(
                messageId = chatAiMessage.messageId,
                chatRoomId = chatAiMessage.chatRoomId.id,
                senderId = chatAiMessage.senderId.id,
                sequence = chatAiMessage.roomSequence.sequence,
                createAt = chatAiMessage.timestamp,
                message = chatAiMessage.text,
                senderType = chatAiMessage.senderType,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatAiLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = this@ChatAiMongoEntity.createAt,
            roomSequence = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), this@ChatAiMongoEntity.sequence),
            text = message,
            type = type,
            senderType = senderType,
        )
    }
}
