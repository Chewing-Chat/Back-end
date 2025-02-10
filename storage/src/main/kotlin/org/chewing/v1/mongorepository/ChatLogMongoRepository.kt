package org.chewing.v1.mongorepository

import org.chewing.v1.model.chat.log.ChatLogType
import org.chewing.v1.mongoentity.ChatMessageMongoEntity
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.mongodb.repository.Update
import org.springframework.data.domain.Pageable

internal interface ChatLogMongoRepository : MongoRepository<ChatMessageMongoEntity, String> {

    @Query("{ \$or: ?0 }")
    fun findByRoomIdAndSeqNumbers(conditions: List<Map<String, Any>>): List<ChatMessageMongoEntity>

    @Modifying
    @Query("{ '_id': ?0 }")
    @Update("{ '\$set': { 'type': ?1 } }")
    fun updateMessageTypeToDelete(messageId: String, deleteType: ChatLogType = ChatLogType.DELETE): Int

    @Query(
        value = "{ 'chatRoomId': ?1, 'type': { \$in: ['NORMAL', 'REPLY'] }, \$text: { \$search: ?0 } }",
        sort = "{ score: { \$meta: 'textScore' } }",
    )
    fun searchByKeywords(keywords: String, chatRoomId: String): List<ChatMessageMongoEntity>

    fun findByChatRoomIdAndSeqNumberLessThanEqualAndSeqNumberGreaterThanOrderBySeqNumberAsc(
        chatRoomId: String,
        sequence: Int,
        joinSequence: Int,
        pageable: Pageable,
    ): List<ChatMessageMongoEntity>

    @Query(
        "{ \$or: [ " +
                " { 'chatRoomId': ?0, 'seqNumber': { \$gt: ?1, \$lte: ?2 } } " +
                "] }"
    )
    fun findByChatRoomIdAndSeqNumberInRange(
        conditions: List<Map<String, Any>>
    ): List<ChatMessageMongoEntity>
}
