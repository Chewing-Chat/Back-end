package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId

interface ChatLogRepository {
    fun readChatMessages(chatRoomId: ChatRoomId, sequence: Int): List<ChatLog>
    fun removeLog(messageId: String) // 메시지 삭제 기능 추가
    fun appendChatLog(chatMessage: ChatMessage)
    fun readChatMessage(messageId: String): ChatLog?
    fun readLatestMessages(chatRoomIds: List<ChatRoomId>): List<ChatLog>
    fun readChatKeyWordMessages(chatRoomId: ChatRoomId, keyword: String): List<ChatLog>
}
