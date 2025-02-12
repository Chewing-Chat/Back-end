package org.chewing.v1.mongoentity

import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.message.ChatInviteMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document(collection = "chat_messages")
internal class ChatInviteMongoEntity(
    messageId: String,
    chatRoomId: String,
    senderId: String,
    seqNumber: Int,
    sendTime: LocalDateTime,
    val targetUserIds: List<String>,
) : ChatMessageMongoEntity(
    messageId = messageId,
    chatRoomId = chatRoomId,
    type = ChatLogType.INVITE,
    senderId = senderId,
    seqNumber = seqNumber,
    sendTime = sendTime,
) {
    companion object {
        fun from(
            chatInviteMessage: ChatInviteMessage,
        ): ChatInviteMongoEntity {
            return ChatInviteMongoEntity(
                messageId = chatInviteMessage.messageId,
                chatRoomId = chatInviteMessage.chatRoomId.id,
                senderId = chatInviteMessage.senderId.id,
                seqNumber = chatInviteMessage.number.sequenceNumber,
                sendTime = chatInviteMessage.timestamp,
                targetUserIds = chatInviteMessage.targetUserIds.map { it.id },
            )
        }
    }

    override fun toChatLog(): ChatLog {
        return ChatInviteLog.of(
            messageId = messageId,
            chatRoomId = ChatRoomId.of(chatRoomId),
            senderId = UserId.of(senderId),
            timestamp = sendTime,
            number = ChatRoomSequence.of(ChatRoomId.of(chatRoomId), seqNumber),
            targetUserIds = targetUserIds.map { UserId.of(it) },
            type = type,
        )
    }
}
