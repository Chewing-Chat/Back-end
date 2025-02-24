package org.chewing.v1.repository

import org.chewing.v1.config.MongoContextTest
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.repository.mongo.chat.room.ChatSequenceRepositoryImpl
import org.chewing.v1.repository.support.MongoDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import java.util.UUID

class ChatSequenceRepositoryTest : MongoContextTest() {
    @Autowired
    private lateinit var mongoTemplate: MongoTemplate

    @Autowired
    private lateinit var mongoDataGenerator: MongoDataGenerator

    private val chatSequenceRepositoryImpl: ChatSequenceRepositoryImpl by lazy {
        ChatSequenceRepositoryImpl(mongoTemplate)
    }

    @Test
    fun `채팅방 시퀀스 번호 읽기 - 채팅방 아이디로 읽기 성공`() {
        val chatRoomId = generateChatRoomId()
        mongoDataGenerator.insertSeqNumber(chatRoomId.id, 1)
        val result = chatSequenceRepositoryImpl.readSequence(chatRoomId)
        assert(result != null)
        assert(result!!.sequenceNumber == 1)
        assert(result.chatRoomId == chatRoomId)
    }

    @Test
    fun `채팅방 시퀀스 번호 읽기 - 채팅방 아이디로 읽기 실패`() {
        val chatRoomId = generateChatRoomId()
        val result = chatSequenceRepositoryImpl.readSequence(chatRoomId)
        assert(result == null)
    }

    @Test
    fun `채팅방들의 시퀀스 번호 읽기 - 채팅방 아이디로 읽기 성공`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()
        mongoDataGenerator.insertSeqNumber(chatRoomId1.id, 1)
        mongoDataGenerator.insertSeqNumber(chatRoomId2.id, 2)
        val result = chatSequenceRepositoryImpl.readsSequences(listOf(chatRoomId1, chatRoomId2))
        assert(result.size == 2)
    }

    @Test
    fun `채팅방 시퀀스 번호 증가 - 없다면 채팅방 아이디로 증가 성공`() {
        val chatRoomId = generateChatRoomId()
        val result = chatSequenceRepositoryImpl.updateIncreaseSequence(chatRoomId)
        assert(result.sequenceNumber == 1)
    }

    @Test
    fun `채팅방 시퀀스 번호 증가 - 있다면 채팅방 아이디로 증가 성공`() {
        val chatRoomId = generateChatRoomId()
        mongoDataGenerator.insertSeqNumber(chatRoomId.id, 1)
        val result = chatSequenceRepositoryImpl.updateIncreaseSequence(chatRoomId)
        assert(result.sequenceNumber == 2)
    }

    @Test
    fun `채팅방 시퀀스 번호 추가 - 채팅방 아이디로 추가 성공`() {
        val chatRoomId = generateChatRoomId()
        val result = chatSequenceRepositoryImpl.appendSequence(chatRoomId)
        assert(result.sequenceNumber == 0)
    }
    private fun generateChatRoomId() = ChatRoomId.of(UUID.randomUUID().toString())
}
