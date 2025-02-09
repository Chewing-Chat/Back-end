package org.chewing.v1.repository.mongo.chat.log

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Repository

@Repository
internal class ChatLogRepositoryImpl(
    private val chatLogMongoRepository: ChatLogMongoRepository,
) : ChatLogRepository {
    override fun readChatMessages(
        chatRoomId: ChatRoomId,
        sequence: Int,
        joinSequence: Int,
    ): List<ChatLog> {
        val pageable: Pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "seqNumber"))
        return chatLogMongoRepository
            .findByChatRoomIdAndSeqNumberLessThanEqualAndSeqNumberGreaterThanOrderBySeqNumberDesc(
                chatRoomId.id,
                sequence,
                joinSequence,
                pageable,
            )
            .map { it.toChatLog() }
    }

    override fun removeLog(messageId: String) {
        chatLogMongoRepository.updateMessageTypeToDelete(messageId)
    }

    override fun appendChatLog(chatMessage: ChatMessage) {
        val entity = ChatMessageMongoEntity.fromChatMessage(chatMessage)
        chatLogMongoRepository.save(entity!!)
    }

    override fun readChatMessage(messageId: String): ChatLog? {
        return chatLogMongoRepository.findById(messageId).map { it.toChatLog() }.orElse(null)
    }

    override fun readLatestMessages(chatRoomIds: List<ChatRoomId>): List<ChatLog> {
        return chatLogMongoRepository.findByRoomIdAndSeqNumbers(
            chatRoomIds.map { mapOf("chatRoomId" to it.id) },
        ).map { it.toChatLog() }
    }

    override fun readChatKeyWordMessages(
        chatRoomId: ChatRoomId,
        keyword: String,
    ): List<ChatLog> {
        return chatLogMongoRepository.searchByKeywords(keyword, chatRoomId.id).map { it.toChatLog() }
    }
}
