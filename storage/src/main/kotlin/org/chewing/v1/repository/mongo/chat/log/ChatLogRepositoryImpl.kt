package org.chewing.v1.repository.mongo.chat.log

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.stereotype.Repository

@Repository
internal class ChatLogRepositoryImpl(
    private val chatLogMongoRepository: ChatLogMongoRepository,
) : ChatLogRepository {
    override fun readChatMessages(
        chatRoomId: ChatRoomId,
        sequence: Int,
    ): List<ChatLog> {
        TODO("Not yet implemented")
    }

    override fun removeLog(messageId: String) {
        TODO("Not yet implemented")
    }

    override fun appendChatLog(chatMessage: ChatMessage) {
        TODO("Not yet implemented")
    }

    override fun readChatMessage(messageId: String): ChatLog? {
        TODO("Not yet implemented")
    }

    override fun readLatestMessages(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        TODO("Not yet implemented")
    }

    override fun readChatKeyWordMessages(
        chatRoomId: ChatRoomId,
        keyword: String,
    ): List<ChatLog> {
        TODO("Not yet implemented")
    }
}
