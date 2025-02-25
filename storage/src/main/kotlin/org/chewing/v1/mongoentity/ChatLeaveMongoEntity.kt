package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatLeaveMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatLeaveMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    sequence: Int,
    createAt: LocalDateTime,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    type = ChatLogType.LEAVE,
    senderId = senderId,
    sequence = sequence,
    createAt = createAt,
) {

    companion object {
        fun from(
            chatLeaveMessage: ChatLeaveMessage,
        ): ChatLeaveMongoEntity {
            return ChatLeaveMongoEntity(
                messageId = chatLeaveMessage.messageId,
                chatRoomId = chatLeaveMessage.chatRoomId.id,
                senderId = chatLeaveMessage.senderId.id,
                sequence = chatLeaveMessage.roomSequence.sequence,
                createAt = chatLeaveMessage.timestamp,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatLeaveLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = this@ChatLeaveMongoEntity.createAt,
            roomSequence = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), this@ChatLeaveMongoEntity.sequence),
            type = type,
        )
    }
}
