package org.chewing.v1.implementation.chat.message

import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.stereotype.Component

@Component
class ChatReader(
    private val chatLogRepository: ChatLogRepository,
) {
    fun readChatLog(chatRoomId: String, page: Int): List<ChatLog> {
        return chatLogRepository.readChatMessages(chatRoomId, page)
    }
    fun readChatKeyWordLog(chatRoomId: String, keyword: String): List<ChatLog> {
        return chatLogRepository.readChatKeyWordMessages(chatRoomId, keyword)
    }

    fun readChatMessage(messageId: String): ChatLog {
        return chatLogRepository.readChatMessage(messageId) ?: throw NotFoundException(ErrorCode.CHATLOG_NOT_FOUND)
    }

    fun readLatestMessages(numbers: List<ChatNumber>): List<ChatLog> {
        return chatLogRepository.readLatestMessages(numbers)
    }
}
