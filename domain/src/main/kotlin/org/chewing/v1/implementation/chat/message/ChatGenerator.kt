package org.chewing.v1.implementation.chat.message

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
class ChatGenerator {
    fun generateReadMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatReadMessage {
        return ChatReadMessage.of(
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            chatRoomType = chatRoomType,
        )
    }

    fun generateDeleteMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        messageId: String,
        chatRoomType: ChatRoomType,
    ): ChatDeleteMessage {
        return ChatDeleteMessage.of(
            targetMessageId = messageId,
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            chatRoomType = chatRoomType,
        )
    }

    fun generateNormalMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        text: String,
        chatRoomType: ChatRoomType,
    ): ChatNormalMessage {
        return ChatNormalMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            text = text,
            chatRoomType = chatRoomType,
        )
    }

    fun generateErrorMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        errorCode: ErrorCode,
        chatRoomType: ChatRoomType,
    ): ChatErrorMessage {
        return ChatErrorMessage.of(
            chatRoomId = chatRoomId,
            errorCode = errorCode,
            userId = userId,
            chatRoomType = chatRoomType,
        )
    }

    fun generateInviteMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        targetUserIds: List<UserId>,
        chatRoomType: ChatRoomType,
    ): ChatInviteMessage {
        return ChatInviteMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            targetUserIds = targetUserIds,
            chatRoomType = chatRoomType,
        )
    }

    fun generateLeaveMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatLeaveMessage {
        return ChatLeaveMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            chatRoomType = chatRoomType,
        )
    }

    fun generateFileMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        medias: List<Media>,
        chatRoomType: ChatRoomType,
    ): ChatFileMessage {
        return ChatFileMessage.of(
            generateKey(chatRoomId),
            chatRoomId = chatRoomId,
            senderId = userId,
            timestamp = LocalDateTime.now(),
            number = number,
            medias = medias,
            chatRoomType = chatRoomType,
        )
    }

    fun generateReplyMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        number: ChatRoomSequence,
        text: String,
        parentLog: ChatLog,
        chatRoomType: ChatRoomType,
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
            parentMessageText = parentMessageText,
            parentSeqNumber = parentLog.number.sequenceNumber,
            parentMessageType = parentLog.type,
            type = MessageType.REPLY,
            chatRoomType = chatRoomType,
        )
    }

    private fun generateKey(chatRoomId: ChatRoomId): String {
        return chatRoomId.id + UUID.randomUUID().toString().substring(0, 8)
    }
}
