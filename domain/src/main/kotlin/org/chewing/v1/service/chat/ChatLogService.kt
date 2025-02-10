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
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.chat.room.ChatRoomSequence
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
        chatRoomNumber: ChatRoomSequence,
        medias: List<Media>,
        chatRoomType: ChatRoomType,
    ): ChatFileMessage {
        val chatMessage = chatGenerator.generateFileMessage(chatRoomId, userId, chatRoomNumber, medias, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun readMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatReadMessage {
        return chatGenerator.generateReadMessage(chatRoomId, userId, chatRoomNumber, chatRoomType)
    }

    fun deleteMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        messageId: String,
        chatRoomType: ChatRoomType,
    ): ChatDeleteMessage {
        val parentMessage = chatReader.readChatMessage(messageId)
        chatValidator.isPossibleDeleteMessage(parentMessage)
        val chatMessage =
            chatGenerator.generateDeleteMessage(chatRoomId, userId, parentMessage.number, messageId, chatRoomType)
        chatRemover.removeChatLog(messageId)
        return chatMessage
    }

    fun replyMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        parentMessageId: String,
        text: String,
        chatRoomNumber: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatReplyMessage {
        val parentMessage = chatReader.readChatMessage(parentMessageId)
        val chatMessage =
            chatGenerator.generateReplyMessage(chatRoomId, userId, chatRoomNumber, text, parentMessage, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun chatNormalMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        text: String,
        chatRoomNumber: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatNormalMessage {
        val chatMessage = chatGenerator.generateNormalMessage(chatRoomId, userId, chatRoomNumber, text, chatRoomType)
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
        number: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatLeaveMessage {
        val chatMessage = chatGenerator.generateLeaveMessage(chatRoomId, userId, number, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessages(
        friendIds: List<UserId>,
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatInviteMessage {
        val chatMessage =
            chatGenerator.generateInviteMessage(chatRoomId, userId, chatRoomNumber, friendIds, chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessage(
        chatRoomId: ChatRoomId,
        friendId: UserId,
        userId: UserId,
        chatRoomNumber: ChatRoomSequence,
        chatRoomType: ChatRoomType,
    ): ChatInviteMessage {
        val chatMessage =
            chatGenerator.generateInviteMessage(chatRoomId, userId, chatRoomNumber, listOf(friendId), chatRoomType)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun getLatestChat(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        return chatReader.readLatestMessages(chatRoomIds)
    }

    fun getUnreadChatLogs(targets: List<UnReadTarget>): List<ChatLog> {
        return chatReader.readsUnreadChatLogs(targets)
    }

    fun getChatLog(chatRoomId: ChatRoomId, sequenceNumber: Int, joinSequence: Int): List<ChatLog> {
        return chatReader.readChatLog(chatRoomId, sequenceNumber, joinSequence)
    }
}
