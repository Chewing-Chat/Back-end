package org.chewing.v1.repository.mongo.chat.log

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.chat.ChatLogRepository
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Repository

@Repository
internal class ChatLogRepositoryImpl(
    private val chatLogMongoRepository: ChatLogMongoRepository,
    private val mongoTemplate: MongoTemplate,
) : ChatLogRepository {
    override fun readChatMessages(chatRoomId: ChatRoomId, sequence: Int, joinSequence: Int): List<ChatLog> {
        val criteria = Criteria
            .where("chatRoomId").`is`(chatRoomId.id)
            .and("seqNumber").lte(sequence).gt(joinSequence)

        val query = Query(criteria)
            .with(Sort.by(Sort.Direction.ASC, "seqNumber"))
            .limit(50)

        return mongoTemplate.find(query, ChatMessageMongoEntity::class.java)
            .map { it.toChatLog() }
    }

    override fun removeLog(messageId: String) {
        chatLogMongoRepository.updateMessageTypeToDelete(messageId)
    }

    override fun appendChatLog(chatMessage: ChatMessage) {
        ChatMessageMongoEntity.fromChatMessage(chatMessage)?. let {
            chatLogMongoRepository.save(it)
        }
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

    override fun readUnreadChatLogs(
        targets: List<UnReadTarget>,
    ): List<ChatLog> {
        val conditions = targets.map { target ->
            mapOf(
                "chatRoomId" to target.chatRoomId.id,
                "seqNumber" to mapOf("\$gt" to target.readSequence, "\$lte" to target.chatRoomSequence),
            )
        }

        return chatLogMongoRepository
            .findByChatRoomIdAndSeqNumberInRange(conditions)
            .map { it.toChatLog() }
    }
}
