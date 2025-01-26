package org.chewing.v1.implementation.chat.message

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class ChatGenerator {
    fun generateReadMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
    ): ChatReadMessage {
        return ChatReadMessage.of(
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
        )
    }

    fun generateDeleteMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
        messageId: String,
    ): ChatDeleteMessage {
        return ChatDeleteMessage.of(
            targetMessageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
        )
    }

    fun generateNormalMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
        text: String,
    ): ChatNormalMessage {
        return ChatNormalMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            text = text,
        )
    }

    fun generateInviteMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
        targetUserIds: List<UserId>,
    ): ChatInviteMessage {
        return ChatInviteMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            targetUserIds = targetUserIds,
        )
    }

    fun generateLeaveMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
    ): ChatLeaveMessage {
        return ChatLeaveMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
        )
    }

    fun generateFileMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
        medias: List<Media>,
    ): ChatFileMessage {
        return ChatFileMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            medias = medias,
        )
    }

    fun generateReplyMessage(
        chatRoomId: String,
        userId: UserId,
        number: ChatLogSequence,
        text: String,
        parentLog: ChatLog,
    ): ChatReplyMessage {
        val (parentMessageId, parentMessageText) = when (parentLog) {
            is ChatNormalLog -> Pair(parentLog.messageId, parentLog.text)
            is ChatReplyLog -> Pair(parentLog.messageId, parentLog.text)
            is ChatFileLog -> Pair(parentLog.messageId, parentLog.medias[0].url)
            else -> throw ConflictException(ErrorCode.INTERNAL_SERVER_ERROR)
        }

        return ChatReplyMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            text = text,
            parentMessageId = parentMessageId,
            parentMessagePage = parentLog.number.page,
            parentMessageText = parentMessageText,
            parentSeqNumber = parentLog.number.sequenceNumber,
            parentMessageType = parentLog.type,
            type = MessageType.REPLY,
        )
    }

    private fun generateKey(chatRoomId: String): String {
        return chatRoomId + UUID.randomUUID().toString().substring(0, 8)
    }
}
