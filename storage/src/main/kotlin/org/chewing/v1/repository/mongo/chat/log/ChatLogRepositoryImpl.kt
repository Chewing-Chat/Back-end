package org.chewing.v1.repository.mongo.chat.log

import org.chewing.v1.model.chat.log.ChatLog
import org.chewing.v1.model.chat.log.UnReadTarget
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.chewing.v1.mongoentity.LatestChatMessageWrapper
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.chewing.v1.repository.chat.ChatLogRepository
import org.chewing.v1.util.SortType
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.aggregation.Aggregation
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
            .and("sequence").lte(sequence).gt(joinSequence)

        val query = Query(criteria)
            .with(SortType.LARGEST.toSort())
            .limit(50)

        return mongoTemplate.find(query, ChatMessageMongoEntity::class.java)
            .map { it.toChatLog() }
    }

    override fun readLatestChatMessages(chatRoomId: ChatRoomId, joinSequence: Int): List<ChatLog> {
        val criteria = Criteria
            .where("chatRoomId").`is`(chatRoomId.id)
            .and("sequence").gt(joinSequence)

        val query = Query(criteria)
            .with(SortType.LARGEST.toSort())
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
        if (chatRoomIds.isEmpty()) {
            return emptyList()
        }
        val chatRoomIdStrings = chatRoomIds.map { it.id }

        // Aggregation 파이프라인:
        // 1. match: 입력된 채팅방 ID들에 해당하는 메시지 필터링
        // 2. sort: sequence를 오름차순 정렬 (가장 작은 값이 첫 번째)
        // 3. group: 각 채팅방별로 그룹화하고, 정렬된 첫 번째 메시지를 latestMessage로 선택
        val aggregation = Aggregation.newAggregation(
            Aggregation.match(Criteria.where("chatRoomId").`in`(chatRoomIdStrings)),
            Aggregation.sort(SortType.LARGEST.toSort()),
            Aggregation.group("chatRoomId").first(Aggregation.ROOT).`as`("latestMessage"),
        )

        val results = mongoTemplate.aggregate(
            aggregation,
            ChatMessageMongoEntity::class.java,
            LatestChatMessageWrapper::class.java,
        )

        return results.mappedResults.map { it.latestMessage.toChatLog() }
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
        if (targets.isEmpty()) {
            return emptyList()
        }
        val conditions = targets.map { target ->
            mapOf(
                "chatRoomId" to target.chatRoomId.id,
                "sequence" to mapOf("\$gt" to target.readSequence, "\$lte" to target.chatRoomSequence),
            )
        }

        return chatLogMongoRepository
            .findByChatRoomIdAndSequenceInRange(conditions, SortType.LARGEST.toSort())
            .map { it.toChatLog() }
    }

    override fun readLatestChatMessage(chatRoomId: ChatRoomId): ChatLog? {
        val query = Query(Criteria.where("chatRoomId").`is`(chatRoomId.id))
            .with(SortType.LARGEST.toSort())
            .limit(1)
        return mongoTemplate.findOne(query, ChatMessageMongoEntity::class.java)?.toChatLog()
    }

    override fun readChatLogsBySender(chatRoomId: ChatRoomId, senderId: UserId): List<ChatLog> {
        val criteria = Criteria
            .where("chatRoomId").`is`(chatRoomId.id)
            .and("senderId").`is`(senderId.id)
            .and("type").`in`("NORMAL", "REPLY", "AI")

        val query = Query(criteria)
            .with(Sort.by(Sort.Direction.ASC, "sequence"))
            .limit(100) // 적절한 범위로 조정

        return mongoTemplate.find(query, ChatMessageMongoEntity::class.java)
            .map { it.toChatLog() }
    }
}
