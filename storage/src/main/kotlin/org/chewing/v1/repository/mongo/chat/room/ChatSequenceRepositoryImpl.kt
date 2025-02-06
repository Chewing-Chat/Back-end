package org.chewing.v1.repository.mongo.chat.room

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.mongoentity.ChatRoomSequenceMongoEntity
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
internal class ChatSequenceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : ChatRoomSequenceRepository {
    override fun readSequence(chatRoomId: ChatRoomId): ChatSequence? {
        val sequenceEntity = mongoTemplate.findById(chatRoomId.id, ChatRoomSequenceMongoEntity::class.java)
        return sequenceEntity?.let {
            ChatSequence.of(chatRoomId, it.seqNumber)
        }
    }

    override fun readsSequences(chatRoomIds: List<ChatRoomId>): List<ChatSequence> {
        val query = Query(Criteria.where("_id").`in`(chatRoomIds.map { it.id }))

        val entities = mongoTemplate.find(query, ChatRoomSequenceMongoEntity::class.java)
        return entities.map { entity ->
            ChatSequence.of(ChatRoomId.of(entity.chatRoomId), entity.seqNumber)
        }
    }

    override fun updateIncreaseSequence(chatRoomId: ChatRoomId): ChatSequence {
        val query = Query(Criteria.where("_id").`is`(chatRoomId))
        val update = Update().inc("seqNumber", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)
        val sequenceEntity =
            mongoTemplate.findAndModify(query, update, options, ChatRoomSequenceMongoEntity::class.java)
        val sequenceNumber = sequenceEntity?.seqNumber ?: 1
        return ChatSequence.of(chatRoomId, sequenceNumber)
    }

    override fun appendSequence(chatRoomId: ChatRoomId) : ChatSequence {
        val entity = ChatRoomSequenceMongoEntity.generate(chatRoomId)
        return mongoTemplate.save(entity).toChatRoomSequence()
    }
}
