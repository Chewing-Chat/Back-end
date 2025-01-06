package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatInviteMessage
import org.chewing.v1.model.chat.room.ChatNumber
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatInviteMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    page: Int,
    sendTime: LocalDateTime,
    val targetUserIds: List<String>,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    senderId = senderId,
    type = ChatLogType.INVITE,
    seqNumber = seqNumber,
    page = page,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatInviteMessage: ChatInviteMessage,
        ): ChatInviteMongoEntity {
            return ChatInviteMongoEntity(
                messageId = chatInviteMessage.messageId,
                chatRoomId = chatInviteMessage.chatRoomId,
                senderId = chatInviteMessage.senderId,
                seqNumber = chatInviteMessage.number.sequenceNumber,
                page = chatInviteMessage.number.page,
                sendTime = chatInviteMessage.timestamp,
                targetUserIds = chatInviteMessage.targetUserIds,
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatInviteLog.of(
            messageId = messageId,
            chatRoomId = chatRoomId,
            senderId = senderId,
            timestamp = sendTime,
            number = ChatNumber.of(chatRoomId, seqNumber, page),
            targetUserIds = targetUserIds,
            type = type,
        )
    }
}
