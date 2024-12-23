package org.chewing.v1.repository.support

import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.chewing.v1.mongoentity.ChatSequenceMongoEntity
import org.chewing.v1.mongorepository.ChatLogMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update

class MongoDataGenerator {
    @Autowired
    private lateinit var chatLogMongoRepository: ChatLogMongoRepository

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    fun chatLogEntityData(chatMessage: ChatMessage) {
        val entity = ChatMessageMongoEntity.fromChatMessage(chatMessage)
        chatLogMongoRepository.save(entity!!)
    }

    fun chatLogEntityDataList(chatMessages: List<ChatMessage>) {
        chatMessages.forEach {
            chatLogEntityData(it)
        }
    }
    fun insertSeqNumber(roomId: String, seqNumber: Long) {
        val query = Query(Criteria.where("chatRoomId").`is`(roomId))
        val update = Update().set("seqNumber", seqNumber)
        mongoTemplate.upsert(query, update, ChatSequenceMongoEntity::class.java)
    }
}
