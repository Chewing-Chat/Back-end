package org.chewing.v1.repository.mongo.chat.member

import org.chewing.v1.model.chat.room.ChatLogSequence
import org.chewing.v1.model.chat.room.ChatRoom
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.DirectChatLogSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.mongoentity.ChatRoomMemberSequenceMongoEntity
import org.chewing.v1.repository.chat.DirectChatRoomMemberSequenceRepository
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Repository

@Repository
class DirectChatRoomMemberSequenceRepositoryImpl(
    private val mongoTemplate: MongoTemplate,
): DirectChatRoomMemberSequenceRepository {

    override fun updateReadSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatLogSequence: DirectChatLogSequence,
    ) {
        val query = Query(
            Criteria.where("chatRoomId").`is`(chatRoomId.id)
                .and("memberId").`is`(userId.id)
        )
        val update = Update().set("readSeqNumber", chatLogSequence.sequenceNumber)
        mongoTemplate.updateFirst(query, update, ChatRoomMemberSequenceMongoEntity::class.java)
    }

    override fun updateJoinSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
        chatLogSequence: DirectChatLogSequence,
    ): DirectChatLogSequence {
        val query = Query(
            Criteria.where("chatRoomId").`is`(chatRoomId.id)
                .and("memberId").`is`(userId.id)
        )

        val update = Update().set("starSeqNumber", chatLogSequence.sequenceNumber)

        // findAndModify로 도큐먼트를 수정하고, 수정된 결과를 받는다.
        val updatedEntity = mongoTemplate.findAndModify(
            query,
            update,
            FindAndModifyOptions.options().returnNew(true),
            ChatRoomMemberSequenceMongoEntity::class.java
        )
        return updatedEntity?.toChatRoomMemberSequence() ?: chatLogSequence
    }
    override fun appendSequence(chatRoomId: ChatRoomId, userId: UserId) {
        val entity = ChatRoomMemberSequenceMongoEntity(chatRoomId.id, userId.id, 0, 0)
        mongoTemplate.save(entity)
    }

    override fun readSequence(
        chatRoomId: ChatRoomId,
        userId: UserId,
    ): DirectChatLogSequence {
        TODO("Not yet implemented")
    }

    override fun readUserRooms(userId: UserId): List<ChatRoom> {
        TODO("Not yet implemented")
    }

    override fun readsSequences(
        chatRoomIds: List<ChatRoomId>,
        userId: UserId,
    ): List<DirectChatLogSequence> {
        TODO("Not yet implemented")
    }
}
