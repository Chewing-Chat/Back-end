package org.chewing.v1.repository.support

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatNumber
import java.time.LocalDateTime

object ChatMessageProvider {
    fun buildNormalMessage(messageId: String, chatRoomId: String): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun buildNormalMessageForSearch(messageId: String, chatRoomId: String, text: String): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = text,
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun buildLeaveMessage(messageId: String, chatRoomId: String): ChatLeaveMessage = ChatLeaveMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun buildInviteMessage(messageId: String, chatRoomId: String): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        targetUserIds = listOf("target"),
        timestamp = LocalDateTime.now(),
    )

    fun buildFileMessage(messageId: String, chatRoomId: String): ChatFileMessage = ChatFileMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        medias = listOf(MediaProvider.buildChatContent()),
    )

    fun buildReplyMessage(messageId: String, chatRoomId: String, normalLog: ChatNormalLog): ChatReplyMessage = ChatReplyMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        parentMessageId = normalLog.messageId,
        parentMessagePage = normalLog.number.page,
        parentMessageText = normalLog.text,
        parentSeqNumber = normalLog.number.sequenceNumber,
        type = MessageType.REPLY,
        parentMessageType = normalLog.type,
    )

    fun buildNormalLog(messageId: String, chatRoomId: String): ChatNormalLog = ChatNormalLog.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        type = ChatLogType.NORMAL,
    )

    fun buildBombMessage(messageId: String, chatRoomId: String): ChatBombMessage = ChatBombMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = "sender",
        text = "text",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        expiredAt = LocalDateTime.now().plusDays(1),
    )

    fun buildReadMessage(chatRoomId: String): ChatReadMessage = ChatReadMessage.of(
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
    )

    fun buildDeleteMessage(chatRoomId: String): ChatDeleteMessage = ChatDeleteMessage.of(
        chatRoomId = chatRoomId,
        senderId = "sender",
        number = ChatNumber.of(chatRoomId, 1, 1),
        timestamp = LocalDateTime.now(),
        targetMessageId = "target",
    )
}
