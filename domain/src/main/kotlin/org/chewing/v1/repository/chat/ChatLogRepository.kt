package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatNumber

interface ChatLogRepository {
    fun readChatMessages(chatRoomId: String, page: Int): List<ChatLog>
    fun removeLog(messageId: String) // 메시지 삭제 기능 추가
    fun appendChatLog(chatMessage: ChatMessage)
    fun readChatMessage(messageId: String): ChatLog?
    fun readLatestMessages(numbers: List<ChatNumber>): List<ChatLog>
    fun readChatKeyWordMessages(chatRoomId: String, keyword: String): List<ChatLog>
}
