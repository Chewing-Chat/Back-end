package org.chewing.v1.repository

import org.chewing.v1.config.MongoContextTest
import org.chewing.v1.repository.mongo.chat.ChatSequenceRepositoryImpl
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
    fun `채팅방 시퀀스 번호 읽기 - 첫 채티방 메시지라면 0 리턴`() {
        val chatRoomId = generateChatRoomId()
        val result = chatSequenceRepositoryImpl.readCurrent(chatRoomId)
        assert(result.sequenceNumber == 0)
    }

    @Test
    fun `채팅방 시퀀스 번호 읽기 - 채팅방 시퀀스 번호가 1인 경우`() {
        val chatRoomId = generateChatRoomId()
        val seqNumber = 1L
        mongoDataGenerator.insertSeqNumber(chatRoomId, seqNumber)
        val result = chatSequenceRepositoryImpl.readCurrent(chatRoomId)
        assert(result.sequenceNumber == seqNumber.toInt())
    }

    @Test
    fun `채팅방 목록 시퀀스 번호 읽기 - 모두 처음 인경우`() {
        val chatRoomIds = generateChatRoomIdList()
        val result = chatSequenceRepositoryImpl.readCurrentSeqNumbers(chatRoomIds)
        assert(result.size == chatRoomIds.size)
        result.forEach { assert(it.sequenceNumber == 0) }
    }

    @Test
    fun `채팅방 목록 시퀀스 번호 읽기 - 시퀀스가 있는 경우`() {
        val chatRoomIds = generateChatRoomIdList()
        val seqNumber = 1L
        chatRoomIds.forEach { mongoDataGenerator.insertSeqNumber(it, seqNumber) }
        val result = chatSequenceRepositoryImpl.readCurrentSeqNumbers(chatRoomIds)
        assert(result.size == chatRoomIds.size)
        result.forEach { assert(it.sequenceNumber == seqNumber.toInt()) }
    }

    @Test
    fun `채팅방 시퀀스 번호 증가 - 채팅방 시퀀스 번호가 1인 경우`() {
        val chatRoomId = generateChatRoomId()
        val result = chatSequenceRepositoryImpl.updateSequenceIncrement(chatRoomId)
        assert(result.sequenceNumber == 1)
    }

    @Test
    fun `채팅방 시퀀스 번호 증가 - 채팅방 시퀀스 번호가 2인 경우`() {
        val chatRoomId = generateChatRoomId()
        val seqNumber = 1L
        mongoDataGenerator.insertSeqNumber(chatRoomId, seqNumber)
        val result = chatSequenceRepositoryImpl.updateSequenceIncrement(chatRoomId)
        assert(result.sequenceNumber == seqNumber.toInt() + 1)
    }

    @Test
    fun `채팅방 목록 시퀀스 번호 증가 - 모두 처음 인경우`() {
        val chatRoomIds = generateChatRoomIdList()
        val result = chatSequenceRepositoryImpl.updateSequenceIncrements(chatRoomIds)
        assert(result.size == chatRoomIds.size)
        result.forEach { assert(it.sequenceNumber == 1) }
    }

    @Test
    fun `채팅방 목록 시퀀스 번호 증가 - 시퀀스가 있는 경우`() {
        val chatRoomIds = generateChatRoomIdList()
        val seqNumber = 1L

        chatRoomIds.forEach { mongoDataGenerator.insertSeqNumber(it, seqNumber) }
        val result = chatSequenceRepositoryImpl.updateSequenceIncrements(chatRoomIds)
        assert(result.size == chatRoomIds.size)
        result.forEach { assert(it.sequenceNumber == seqNumber.toInt() + 1) }
    }

    private fun generateChatRoomId() = UUID.randomUUID().toString()

    private fun generateChatRoomIdList() = listOf(generateChatRoomId(), generateChatRoomId(), generateChatRoomId())
}
