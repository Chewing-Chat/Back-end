package org.chewing.v1.repository.mongo.chat

import org.chewing.v1.model.chat.room.ChatSequenceNumber
import org.chewing.v1.mongoentity.ChatSequenceMongoEntity
import org.chewing.v1.repository.chat.ChatSequenceRepository
import org.springframework.data.mongodb.core.BulkOperations
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
internal class ChatSequenceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : ChatSequenceRepository {

    override fun readCurrent(chatRoomId: String): ChatSequenceNumber {
        val sequenceEntity = mongoTemplate.findById(chatRoomId, ChatSequenceMongoEntity::class.java)
        return sequenceEntity?.let {
            ChatSequenceNumber.of(it.seqNumber, chatRoomId)
        } ?: ChatSequenceNumber.of(0, chatRoomId)
    }

    override fun updateSequenceIncrement(chatRoomId: String): ChatSequenceNumber {
        val query = Query(Criteria.where("_id").`is`(chatRoomId))
        val update = Update().inc("seqNumber", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)
        val sequenceEntity = mongoTemplate.findAndModify(query, update, options, ChatSequenceMongoEntity::class.java)
        val sequenceNumber = sequenceEntity?.seqNumber ?: 1
        return ChatSequenceNumber.of(sequenceNumber, chatRoomId)
    }

    override fun updateSequenceIncrements(chatRoomIds: List<String>): List<ChatSequenceNumber> {
        val bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, ChatSequenceMongoEntity::class.java)

        chatRoomIds.forEach { roomId ->
            val query = Query(Criteria.where("_id").`is`(roomId))
            val update = Update().inc("seqNumber", 1)
            bulkOperations.updateOne(query, update)
        }

        // 일괄 업데이트 수행
        bulkOperations.execute()

        // 업데이트된 값들을 조회하여 ChatSequenceNumber 리스트로 변환
        return chatRoomIds.map { roomId ->
            val query = Query(Criteria.where("_id").`is`(roomId))
            val sequenceEntity = mongoTemplate.findOne(query, ChatSequenceMongoEntity::class.java)
            val sequenceNumber = sequenceEntity?.seqNumber ?: 1
            ChatSequenceNumber.of(sequenceNumber, roomId)
        }
    }

    override fun readCurrentSeqNumbers(chatRoomIds: List<String>): List<ChatSequenceNumber> {
        // `_id` 필드를 기준으로 조회 (chatRoomId가 `_id` 필드임)
        val query = Query(Criteria.where("_id").`in`(chatRoomIds))
        val entities = mongoTemplate.find(query, ChatSequenceMongoEntity::class.java)

        // 조회된 엔티티를 Map으로 변환 (chatRoomId를 키로 함)
        val entityMap = entities.associateBy { it.chatRoomId }

        // chatRoomIds와 entityMap을 대조하여 시퀀스 번호 생성
        return chatRoomIds.map { chatRoomId ->
            val entity = entityMap[chatRoomId]
            ChatSequenceNumber.of(entity?.seqNumber ?: 0, chatRoomId) // 없으면 0 반환
        }
    }
}
