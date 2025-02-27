package org.chewing.v1.mongorepository

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update

internal interface ChatLogMongoRepository : MongoRepository<ChatMessageMongoEntity, String> {

    @Query("{ \$or: ?0 }")
    fun findByRoomIdAndSequences(conditions: List<Map<String, Any>>, sort: Sort): List<ChatMessageMongoEntity>

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '\$set': { 'type': ?1 } }")
    fun updateMessageTypeToDelete(messageId: String, deleteType: ChatLogType = ChatLogType.DELETE): Int

    @Query(
        value = "{ 'chatRoomId': ?1, 'type': { \$in: ['NORMAL', 'REPLY'] }, \$text: { \$search: ?0 } }",
        sort = "{ 'sequence': -1 }",
    )
    fun searchByKeywords(keywords: String, chatRoomId: String): List<ChatMessageMongoEntity>

    @Query("{ \$or: ?0 }")
    fun findByChatRoomIdAndSequenceInRange(
        conditions: List<Map<String, Any>>,
    ): List<ChatMessageMongoEntity>

    fun findFirstByChatRoomIdOrderBySequenceDesc(chatRoomId: String): ChatMessageMongoEntity?
}
