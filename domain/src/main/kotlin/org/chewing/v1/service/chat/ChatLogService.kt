package org.chewing.v1.service.chat

import org.chewing.v1.implementation.chat.message.ChatAppender
import org.chewing.v1.implementation.chat.message.ChatGenerator
import org.chewing.v1.implementation.chat.message.ChatReader
import org.chewing.v1.implementation.chat.message.ChatRemover
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.*
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
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
) {
    fun uploadFiles(fileDataList: List<FileData>, userId: UserId): List<Media> {
        return fileHandler.handleNewFiles(userId, fileDataList, FileCategory.CHAT)
    }

    fun mediasMessage(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatRoomNumber: ChatSequence,
        medias: List<Media>,
    ): ChatFileMessage {
        val chatMessage = chatGenerator.generateFileMessage(chatRoomId, userId, chatRoomNumber, medias)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun readMessage(chatRoomId: ChatRoomId, userId: UserId, chatRoomNumber: ChatSequence): ChatReadMessage {
        return chatGenerator.generateReadMessage(chatRoomId, userId, chatRoomNumber)
    }

    fun deleteMessage(chatRoomId: ChatRoomId, userId: UserId, messageId: String, chatRoomNumber: ChatSequence): ChatDeleteMessage {
        val chatMessage = chatGenerator.generateDeleteMessage(chatRoomId, userId, chatRoomNumber, messageId)
        chatRemover.removeChatLog(messageId)
        return chatMessage
    }

    fun replyMessage(chatRoomId: ChatRoomId, userId: UserId, parentMessageId: String, text: String, chatRoomNumber: ChatSequence): ChatReplyMessage {
        val parentMessage = chatReader.readChatMessage(parentMessageId)
        val chatMessage = chatGenerator.generateReplyMessage(chatRoomId, userId, chatRoomNumber, text, parentMessage)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun chatNormalMessage(chatRoomId: ChatRoomId, userId: UserId, text: String, chatRoomNumber: ChatSequence): ChatNormalMessage {
        val chatMessage = chatGenerator.generateNormalMessage(chatRoomId, userId, chatRoomNumber, text)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun leaveMessages(userId: UserId, numbers: List<ChatSequence>): List<ChatLeaveMessage> {
        return numbers.map { number ->
            val chatMessage = chatGenerator.generateLeaveMessage(number.chatRoomId, userId, number)
            chatAppender.appendChatLog(chatMessage)
            chatMessage
        }
    }

    fun inviteMessages(friendIds: List<UserId>, chatRoomId: ChatRoomId, userId: UserId, chatRoomNumber: ChatSequence): ChatInviteMessage {
        val chatMessage = chatGenerator.generateInviteMessage(chatRoomId, userId, chatRoomNumber, friendIds)
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun inviteMessage(chatRoomId: ChatRoomId, friendId: UserId, userId: UserId, chatRoomNumber: ChatSequence): ChatInviteMessage {
        val chatMessage = chatGenerator.generateInviteMessage(chatRoomId, userId, chatRoomNumber, listOf(friendId))
        chatAppender.appendChatLog(chatMessage)
        return chatMessage
    }

    fun getLatestChat(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        return chatReader.readLatestMessages(chatRoomIds)
    }

    fun getChatLog(chatRoomId: ChatRoomId, sequenceNumber: Int, userStartSequence: Int): List<ChatLog> {
        val chatLogs = chatReader.readChatLog(chatRoomId, sequenceNumber)
        return chatLogs.filter { it.number.sequenceNumber > userStartSequence }
    }
}
