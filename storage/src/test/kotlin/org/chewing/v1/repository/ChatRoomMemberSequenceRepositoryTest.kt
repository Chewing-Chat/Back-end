package org.chewing.v1.repository

import org.chewing.v1.config.MongoContextTest
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.mongo.chat.member.ChatRoomMemberSequenceRepositoryImpl
import org.chewing.v1.repository.support.MongoDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import java.util.UUID

class ChatRoomMemberSequenceRepositoryTest : MongoContextTest() {

    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var mongoDataGenerator: MongoDataGenerator

    private val chatRoomMemberSequenceRepositoryImpl: ChatRoomMemberSequenceRepositoryImpl by lazy {
        ChatRoomMemberSequenceRepositoryImpl(mongoTemplate)
    }

    @Test
    fun `읽기 시퀀스를 업데이트 - 생성`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val sequenceNumber = 1
        mongoDataGenerator.insertMemberSeqNumber(chatRoomId.id, userId.id, 0, 0)
        val result = chatRoomMemberSequenceRepositoryImpl.updateReadSequence(chatRoomId, userId, sequenceNumber)
        assert(result != null)
        assert(result!!.readSequenceNumber == sequenceNumber)
    }

    @Test
    fun `읽기 시퀀스를 업데이트 - 실패 - 없음`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val sequenceNumber = 1
        val result = chatRoomMemberSequenceRepositoryImpl.updateReadSequence(chatRoomId, userId, sequenceNumber + 1)
        assert(result == null)
    }

    @Test
    fun `입장 시퀀스를 업데이트 - 성공`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val sequenceNumber = 1
        mongoDataGenerator.insertMemberSeqNumber(chatRoomId.id, userId.id, 0, 0)
        val result = chatRoomMemberSequenceRepositoryImpl.updateJoinSequence(chatRoomId, userId, ChatRoomSequence.of(chatRoomId, sequenceNumber.plus(1)))
        assert(result != null)
        assert(result!!.joinSequenceNumber == sequenceNumber.plus(1))
        assert(result.readSequenceNumber == sequenceNumber.plus(1))
    }

    @Test
    fun `입장 시퀀스를 업데이트 - 실패 - 없음`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val sequenceNumber = 1
        val result = chatRoomMemberSequenceRepositoryImpl.updateJoinSequence(chatRoomId, userId, ChatRoomSequence.of(chatRoomId, sequenceNumber.plus(1)))
        assert(result == null)
    }

    @Test
    fun `맴버의 시퀀스 정보가 포함된 모든 채팅방 시퀀스를 조회`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()
        val chatRoomIds = listOf(chatRoomId1, chatRoomId2)
        val userId = generateUserId()
        val sequenceNumber = 1L
        mongoDataGenerator.insertMemberSeqNumber(chatRoomId1.id, userId.id, sequenceNumber, sequenceNumber)
        mongoDataGenerator.insertMemberSeqNumber(chatRoomId2.id, userId.id, sequenceNumber, sequenceNumber)
        val result = chatRoomMemberSequenceRepositoryImpl.readsSequences(chatRoomIds, userId)
        assert(result.size == 2)
    }

    @Test
    fun `맴버의 시퀀스 정보가 포함된 채팅방 시퀀스를 조회`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val sequenceNumber = 1L
        mongoDataGenerator.insertMemberSeqNumber(chatRoomId.id, userId.id, sequenceNumber, sequenceNumber)
        val result = chatRoomMemberSequenceRepositoryImpl.readSequence(chatRoomId, userId)
        assert(result != null)
        assert(result!!.chatRoomId == chatRoomId)
        assert(result.joinSequenceNumber == sequenceNumber.toInt())
        assert(result.readSequenceNumber == sequenceNumber.toInt())
    }

    @Test
    fun `맴버의 시퀀스 생성`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        chatRoomMemberSequenceRepositoryImpl.appendSequence(chatRoomId, userId)
        val result = chatRoomMemberSequenceRepositoryImpl.readSequence(chatRoomId, userId)
        assert(result != null)
        assert(result!!.chatRoomId == chatRoomId)
    }

    private fun generateChatRoomId() = ChatRoomId.of(UUID.randomUUID().toString())
    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())
}
