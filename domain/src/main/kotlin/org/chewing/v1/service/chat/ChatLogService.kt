package org.chewing.v1.service.chat

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.implementation.chat.message.ChatAppender
import org.chewing.v1.implementation.chat.message.ChatGenerator
import org.chewing.v1.implementation.chat.message.ChatReader
import org.chewing.v1.implementation.chat.message.ChatRemover
import org.chewing.v1.implementation.chat.message.ChatValidator
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.feed.Feed
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.chewing.v1.model.media.Media
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class ChatLogService(
    private val fileHandler: FileHandler,
    private val chatAppender: ChatAppender,
    private val chatReader: ChatReader,
    private val chatGenerator: ChatGenerator,
    private val chatRemover: ChatRemover,
    private val chatValidator: ChatValidator,
) {
    fun uploadFiles(fileDataList: List<FileData>, userId: UserId): List<Media> {
        return fileHandler.handleNewFiles(userId, fileDataList, FileCategory.CHAT)
    }

    fun mediasMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        medias: List<Media>,
        chatRoomType: ChatRoomType,
    ): ChatFileMessage {
        val chatMessage = chatGenerator.generateFileMessage(chatRoomId, userId, roomSequence, medias, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun readMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatReadMessage {
        return chatGenerator.generateReadMessage(chatRoomId, userId, roomSequence, chatRoomType)
    }

    fun deleteMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        messageId: String,
        chatRoomType: ChatRoomType,
    ): ChatDeleteMessage {
        val parentLog = chatReader.readChatMessage(messageId)
        chatValidator.isPossibleDeleteMessage(parentLog)
        val chatMessage =
            chatGenerator.generateDeleteMessage(chatRoomId, userId, parentLog.roomSequence, messageId, chatRoomType)
        chatRemover.removeChatLog(messageId)
        return chatMessage
    }

    fun replyMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        parentMessageId: String,
        text: String,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatReplyMessage {
        val parentMessage = chatReader.readChatMessage(parentMessageId)
        val chatMessage =
            chatGenerator.generateReplyMessage(chatRoomId, userId, roomSequence, text, parentMessage, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun chatNormalMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        text: String,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatNormalMessage {
        val chatMessage = chatGenerator.generateNormalMessage(chatRoomId, userId, roomSequence, text, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun chatErrorMessages(
        chatRoomId: ChatRoomId,
        userId: UserId,
        errorCode: ErrorCode,
        chatRoomType: ChatRoomType,
    ): ChatErrorMessage {
        return chatGenerator.generateErrorMessage(chatRoomId, userId, errorCode, chatRoomType)
    }

    fun leaveMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatLeaveMessage {
        val chatMessage = chatGenerator.generateLeaveMessage(chatRoomId, userId, roomSequence, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessages(
        friendIds: List<UserId>,
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatInviteMessage {
        val chatMessage =
            chatGenerator.generateInviteMessage(chatRoomId, userId, roomSequence, friendIds, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessage(
        chatRoomId: ChatRoomId,
        friendId: UserId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatInviteMessage {
        val chatMessage =
            chatGenerator.generateInviteMessage(chatRoomId, userId, roomSequence, listOf(friendId), chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun commentMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        comment: String,
        chatRoomType: ChatRoomType,
        feed: Feed,
    ): ChatCommentMessage {
        val chatMessage = chatGenerator.generateCommentMessage(chatRoomId, userId, roomSequence, comment, chatRoomType, feed)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun aiMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        roomSequence: ChatRoomSequence,
        text: String,
        chatRoomType: ChatRoomType,
        senderType: SenderType,
    ) {
        val chatMessage = chatGenerator.generateAiMessage(chatRoomId, userId, roomSequence, text, chatRoomType, senderType)
        chatAppender.appendChatLog(chatMessage)
    }

    fun getsLatestChatLog(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        return chatReader.readLatestMessages(chatRoomIds)
    }

    fun getLatestChatLog(chatRoomId: ChatRoomId): ChatLog {
        return chatReader.readLatestMessage(chatRoomId)
    }

    fun getUnreadChatLogs(targets: List<UnReadTarget>): List<ChatLog> {
        return chatReader.readsUnreadChatLogs(targets)
    }

    fun getChatKeyWordMessages(chatRoomId: ChatRoomId, keyword: String): List<ChatLog> {
        return chatReader.readChatKeyWordMessages(chatRoomId, keyword)
    }

    fun getChatLogs(chatRoomId: ChatRoomId, targetSequence: Int, joinSequence: Int): List<ChatLog> {
        return chatReader.readChatLog(chatRoomId, targetSequence, joinSequence)
    }

    fun getLatestChatLogs(chatRoomId: ChatRoomId, joinSequence: Int): List<ChatLog> {
        return chatReader.readLatestChatLogs(chatRoomId, joinSequence)
    }
    fun getChatLog(messageId: String): ChatLog {
        return chatReader.readChatMessage(messageId)
    }
}
