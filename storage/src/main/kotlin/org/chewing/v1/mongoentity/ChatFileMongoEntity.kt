package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatFileMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatFileMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    sendTime: LocalDateTime,
    val medias: List<Media>,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.FILE,
    seqNumber = seqNumber,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatFileMessage: ChatFileMessage,
        ): ChatFileMongoEntity {
            return ChatFileMongoEntity(
                chatFileMessage.messageId,
                chatFileMessage.chatRoomId.id,
                chatFileMessage.senderId.id,
                chatFileMessage.number.sequenceNumber,
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
            timestamp = sendTime,
            number = ChatSequence.of(ChatRoomId.of(chatRoomId), seqNumber),
            medias = medias,
            type = type,
        )
    }
}
