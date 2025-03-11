package org.chewing.v1.repository.chat

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId

interface ChatLogRepository {
    fun readChatMessages(chatRoomId: ChatRoomId, sequence: Int, joinSequence: Int): List<ChatLog>
    fun readLatestChatMessages(chatRoomId: ChatRoomId, joinSequence: Int): List<ChatLog>
    fun removeLog(messageId: String)
    fun appendChatLog(chatMessage: ChatMessage)
    fun readChatMessage(messageId: String): ChatLog?
    fun readLatestMessages(chatRoomIds: List<ChatRoomId>): List<ChatLog>
    fun readChatKeyWordMessages(chatRoomId: ChatRoomId, keyword: String): List<ChatLog>
    fun readUnreadChatLogs(targets: List<UnReadTarget>): List<ChatLog>
    fun readLatestChatMessage(chatRoomId: ChatRoomId): ChatLog?
}
