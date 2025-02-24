package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatFileMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatFileMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    sequence: Int,
    createAt: LocalDateTime,
    val medias: List<Media>,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.FILE,
    sequence = sequence,
    createAt = createAt,
) {
    companion object {
        fun from(
            chatFileMessage: ChatFileMessage,
        ): ChatFileMongoEntity {
            return ChatFileMongoEntity(
                chatFileMessage.messageId,
                chatFileMessage.chatRoomId.id,
                chatFileMessage.senderId.id,
                chatFileMessage.roomSequence.sequenceNumber,
                chatFileMessage.timestamp,
                chatFileMessage.medias,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatFileLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = this@ChatFileMongoEntity.createAt,
            roomSequence = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), this@ChatFileMongoEntity.sequence),
            medias = medias,
            type = type,
        )
    }
}
