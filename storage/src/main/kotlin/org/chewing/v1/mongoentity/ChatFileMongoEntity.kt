package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatFileMessage
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.model.media.Media
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatFileMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    page: Int,
    sendTime: LocalDateTime,
    val medias: List<Media>,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.FILE,
    seqNumber = seqNumber,
    page = page,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatFileMessage: ChatFileMessage,
        ): ChatFileMongoEntity {
            return ChatFileMongoEntity(
                chatFileMessage.messageId,
                chatFileMessage.chatRoomId,
                chatFileMessage.senderId,
                chatFileMessage.number.sequenceNumber,
                chatFileMessage.number.page,
                chatFileMessage.timestamp,
                chatFileMessage.medias,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatFileLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = senderId,
            timestamp = sendTime,
            number = ChatNumber.of(chatRoomId, seqNumber, page),
            medias = medias,
            type = type,
        )
    }
}
