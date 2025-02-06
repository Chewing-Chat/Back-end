package org.chewing.v1.repository.mongo.chat.member

import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.mongoentity.ChatRoomMemberSequenceMongoEntity
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class ChatRoomMemberSequenceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
) : ChatRoomMemberSequenceRepository {

    override fun updateReadSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatLogSequence: ChatSequence,
    ): ChatSequence {
        val query = Query(
            Criteria.where("chatRoomId").`is`(chatRoomId.id)
                .and("memberId").`is`(userId.id),
        )

        val update = Update()
            .set("readSeqNumber", chatLogSequence.sequenceNumber)
            .setOnInsert("chatRoomId", chatRoomId.id)
            .setOnInsert("memberId", userId.id)

        val options = FindAndModifyOptions.options()
            .upsert(false)
            .returnNew(true)

        val updatedEntity = mongoTemplate.findAndModify(
            query,
            update,
            options,
            ChatRoomMemberSequenceMongoEntity::class.java,
        )

        return updatedEntity?.toChatRoomMemberSequence() ?: chatLogSequence
    }

    override fun updateJoinSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatLogSequence: ChatSequence,
    ): ChatSequence {
        val query = Query(
            Criteria.where("chatRoomId").`is`(chatRoomId.id)
                .and("memberId").`is`(userId.id),
        )

        val options = FindAndModifyOptions.options()
            .upsert(false)
            .returnNew(true)

        val update = Update()
            .set("joinSeqNumber", chatLogSequence.sequenceNumber)
            .setOnInsert("chatRoomId", chatRoomId.id)
            .setOnInsert("memberId", userId.id)

        val updatedEntity = mongoTemplate.findAndModify(
            query,
            update,
            options,
            ChatRoomMemberSequenceMongoEntity::class.java,
        )

        return updatedEntity?.toChatRoomMemberSequence() ?: chatLogSequence
    }

    override fun readsSequences(
        chatRoomIds: List<ChatRoomId>,
        userId: UserId,
    ): List<ChatSequence> {
        val roomIdList = chatRoomIds.map { it.id }

        // 조건: chatRoomId in (roomIdList) AND memberId == userId
        val query = Query(
            Criteria.where("chatRoomId").`in`(roomIdList)
                .and("memberId").`is`(userId.id),
        )
        // DB에서 해당 도큐먼트들을 조회
        return mongoTemplate.find(query, ChatRoomMemberSequenceMongoEntity::class.java).map { it.toChatRoomMemberSequence() }
    }

    override fun readSequences(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): ChatSequence? {
        val query = Query(
            Criteria.where("chatRoomId").`is`(chatRoomId.id)
                .and("memberId").`is`(userId.id),
        )

        return mongoTemplate.findOne(query, ChatRoomMemberSequenceMongoEntity::class.java)?.toChatRoomMemberSequence()
    }

    override fun appendSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ) {
        val entity = ChatRoomMemberSequenceMongoEntity.generate(
            chatRoomId = chatRoomId,
            memberId = userId,
        )
        mongoTemplate.save(entity)
    }
}
