package org.chewing.v1.repository.mongo.chat.room

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
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

    override fun readSequence(chatRoomId: ChatRoomId): ChatRoomSequence? {
        val sequenceEntity = mongoTemplate.findById(chatRoomId.id, ChatRoomSequenceMongoEntity::class.java)
        return sequenceEntity?.let {
            ChatRoomSequence.of(chatRoomId, it.sequence)
        }
    }

    override fun readsSequences(chatRoomIds: List<ChatRoomId>): List<ChatRoomSequence> {
        if (chatRoomIds.isEmpty()) {
            return emptyList()
        }

        val query = Query(Criteria.where("_id").`in`(chatRoomIds.map { it.id }))

        val entities = mongoTemplate.find(query, ChatRoomSequenceMongoEntity::class.java)
        return entities.map { entity ->
            ChatRoomSequence.of(ChatRoomId.of(entity.chatRoomId), entity.sequence)
        }
    }

    override fun updateIncreaseSequence(chatRoomId: ChatRoomId): ChatRoomSequence {
        val query = Query(Criteria.where("_id").`is`(chatRoomId.id))
        val update = Update().inc("sequence", 1)
        val options = FindAndModifyOptions.options().returnNew(true).upsert(true)
        val sequenceEntity =
            mongoTemplate.findAndModify(query, update, options, ChatRoomSequenceMongoEntity::class.java)
        val sequenceNumber = sequenceEntity?.sequence ?: 1
        return ChatRoomSequence.of(chatRoomId, sequenceNumber)
    }

    override fun appendSequence(chatRoomId: ChatRoomId) {
        val entity = ChatRoomSequenceMongoEntity.generate(chatRoomId)
        mongoTemplate.save(entity).toChatRoomSequence()
    }
}
