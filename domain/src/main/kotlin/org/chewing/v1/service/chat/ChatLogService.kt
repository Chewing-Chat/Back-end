package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.message.ChatAppender
import org.chewing.v1.implementation.chat.message.ChatGenerator
import org.chewing.v1.implementation.chat.message.ChatReader
import org.chewing.v1.implementation.chat.message.ChatRemover
import org.chewing.v1.implementation.chat.sequence.ChatFinder
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.model.media.FileData
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ChatLogService(
    private val fileHandler: FileHandler,
    private val chatFinder: ChatFinder,
    private val chatAppender: ChatAppender,
    private val chatReader: ChatReader,
    private val chatGenerator: ChatGenerator,
    private val chatRemover: ChatRemover,
) {
    fun uploadFiles(fileDataList: List<FileData>, userId: String, chatRoomId: String): ChatFileMessage {
        val medias = fileHandler.handleNewFiles(userId, fileDataList, FileCategory.CHAT)
        val chatRoomNumber = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateFileMessage(chatRoomId, userId, chatRoomNumber, medias)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun readMessage(chatRoomId: String, userId: String): ChatReadMessage {
        val chatRoomNumber = chatFinder.findCurrentNumber(chatRoomId)
        return chatGenerator.generateReadMessage(chatRoomId, userId, chatRoomNumber)
    }

    fun deleteMessage(chatRoomId: String, userId: String, messageId: String): ChatDeleteMessage {
        val chatRoomNumber = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateDeleteMessage(chatRoomId, userId, chatRoomNumber, messageId)
        chatRemover.removeChatLog(messageId)
        return chatMessage
    }

    fun replyMessage(chatRoomId: String, userId: String, parentMessageId: String, text: String): ChatReplyMessage {
        val chatRoomNumber = chatFinder.findNextNumber(chatRoomId)
        val parentMessage = chatReader.readChatMessage(parentMessageId)
        val chatMessage = chatGenerator.generateReplyMessage(chatRoomId, userId, chatRoomNumber, text, parentMessage)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun chatNormalMessage(chatRoomId: String, userId: String, text: String): ChatNormalMessage {
        val chatRoomNumber = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateNormalMessage(chatRoomId, userId, chatRoomNumber, text)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun leaveMessages(chatRoomIds: List<String>, userId: String): List<ChatLeaveMessage> {
        return chatFinder.findNextNumbers(chatRoomIds).map { number ->
            val chatMessage = chatGenerator.generateLeaveMessage(number.chatRoomId, userId, number)
            chatAppender.appendChatLog(chatMessage)
            chatMessage
        }
    }

    fun inviteMessages(friendIds: List<String>, chatRoomId: String, userId: String): ChatInviteMessage {
        val number = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateInviteMessage(chatRoomId, userId, number, friendIds)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessage(chatRoomId: String, friendId: String, userId: String): ChatInviteMessage {
        val number = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateInviteMessage(chatRoomId, userId, number, listOf(friendId))
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun bombingMessage(chatRoomId: String, userId: String, text: String, expiredAt: LocalDateTime): ChatBombMessage {
        val number = chatFinder.findNextNumber(chatRoomId)
        val chatMessage = chatGenerator.generateBombMessage(chatRoomId, userId, number, text, expiredAt)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun getLatestChat(chatRoomIds: List<String>): List<ChatLog> {
        val chatNumbers = chatFinder.findCurrentNumbers(chatRoomIds)
        return chatReader.readLatestMessages(chatNumbers)
    }

    fun getChatLog(chatRoomId: String, page: Int): List<ChatLog> {
        return chatReader.readChatLog(chatRoomId, page)
    }
    fun getChatKeyWordLog(chatRoomId: String, resultKeyword: String): List<ChatLog> {
        return chatReader.readChatKeyWordLog(chatRoomId, resultKeyword)
    }
}
