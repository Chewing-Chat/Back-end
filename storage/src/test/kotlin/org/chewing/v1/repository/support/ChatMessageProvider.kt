package org.chewing.v1.repository.support

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime

object ChatMessageProvider {
    fun buildNormalMessage(messageId: String, chatRoomId: ChatRoomId): ChatNormalMessage = ChatNormalMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildLeaveMessage(messageId: String, chatRoomId: ChatRoomId): ChatLeaveMessage = ChatLeaveMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildInviteMessage(messageId: String, chatRoomId: ChatRoomId): ChatInviteMessage = ChatInviteMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        targetUserIds = listOf(UserId.of("target")),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildFileMessage(messageId: String, chatRoomId: ChatRoomId): ChatFileMessage = ChatFileMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        medias = listOf(MediaProvider.buildChatContent()),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildReplyMessage(messageId: String, chatRoomId: ChatRoomId, normalLog: ChatNormalLog): ChatReplyMessage = ChatReplyMessage.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        parentMessageId = normalLog.messageId,
        parentMessageText = normalLog.text,
        parentSeqNumber = normalLog.number.sequenceNumber,
        type = MessageType.REPLY,
        parentMessageType = normalLog.type,
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildNormalLog(messageId: String, chatRoomId: ChatRoomId): ChatNormalLog = ChatNormalLog.of(
        messageId = messageId,
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        text = "text",
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        type = ChatLogType.NORMAL,
    )

    fun buildReadMessage(chatRoomId: ChatRoomId): ChatReadMessage = ChatReadMessage.of(
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        chatRoomType = ChatRoomType.DIRECT,
    )

    fun buildDeleteMessage(chatRoomId: ChatRoomId): ChatDeleteMessage = ChatDeleteMessage.of(
        chatRoomId = chatRoomId,
        senderId = UserId.of("sender"),
        number = ChatRoomSequence.of(chatRoomId, 1),
        timestamp = LocalDateTime.now(),
        targetMessageId = "target",
        chatRoomType = ChatRoomType.DIRECT,
    )
}
