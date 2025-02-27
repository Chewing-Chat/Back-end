package org.chewing.v1.implementation.chat.message

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.stereotype.Component

@Component
class ChatReader(
    private val chatLogRepository: ChatLogRepository,
) {
    fun readChatLog(chatRoomId: ChatRoomId, targetSequence: Int, joinSequence: Int): List<ChatLog> {
        return chatLogRepository.readChatMessages(chatRoomId, targetSequence, joinSequence)
    }

    fun readLatestChatLogs(chatRoomId: ChatRoomId, joinSequence: Int): List<ChatLog> {
        return chatLogRepository.readLatestChatMessages(chatRoomId, joinSequence)
    }

    fun readChatMessage(messageId: String): ChatLog {
        return chatLogRepository.readChatMessage(messageId) ?: throw NotFoundException(ErrorCode.CHATLOG_NOT_FOUND)
    }

    fun readLatestMessages(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        return chatLogRepository.readLatestMessages(chatRoomIds)
    }

    fun readLatestMessage(chatRoomId: ChatRoomId): ChatLog {
        return chatLogRepository.readLatestChatMessage(chatRoomId)
            ?: throw NotFoundException(ErrorCode.CHATLOG_NOT_FOUND)
    }

    fun readsUnreadChatLogs(targets: List<UnReadTarget>): List<ChatLog> {
        return chatLogRepository.readUnreadChatLogs(targets)
    }

    fun readChatKeyWordMessages(chatRoomId: ChatRoomId, keyword: String): List<ChatLog> {
        return chatLogRepository.readChatKeyWordMessages(chatRoomId, keyword)
    }
}
