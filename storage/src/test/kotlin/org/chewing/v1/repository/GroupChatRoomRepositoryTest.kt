package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.chat.GroupChatRoomJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.repository.jpa.chat.GroupChatRoomRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class GroupChatRoomRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var groupChatRoomJpaRepository: GroupChatRoomJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var groupChatRoomRepositoryImpl: GroupChatRoomRepositoryImpl

    @Test
    fun `그룹 채팅방 생성`() {
        val groupName = "group"
        val result = groupChatRoomRepositoryImpl.append(groupName)
        val room = groupChatRoomJpaRepository.findById(result.id)
        assert(room.isPresent)
        val roomInfo = room.get().toChatRoom()
        assert(roomInfo.name == groupName)
    }

    @Test
    fun `그룹 채팅방 조회`() {
        val groupName = "group"
        val result = jpaDataGenerator.groupChatRoomEntityData(groupName)
        val room = groupChatRoomRepositoryImpl.readRoomInfo(result.chatRoomId)
        assert(room != null)
        assert(room!!.name == groupName)
    }

    @Test
    fun `그룹 채팅방들 조회`() {
        val groupName1 = "group1"
        val groupName2 = "group2"
        val result1 = jpaDataGenerator.groupChatRoomEntityData(groupName1)
        val result2 = jpaDataGenerator.groupChatRoomEntityData(groupName2)
        val room = groupChatRoomRepositoryImpl.readRoomInfos(listOf(result1.chatRoomId, result2.chatRoomId))
        assert(room.size == 2)
    }

    @Test
    fun `그룹 채팅방 조회 실패`() {
        val chatRoomId = generateChatRoomId()
        val room = groupChatRoomRepositoryImpl.readRoomInfo(chatRoomId)
        assert(room == null)
    }

    @Test
    fun `그룹 채팅방들 조회 실패`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()
        val room = groupChatRoomRepositoryImpl.readRoomInfos(listOf(chatRoomId1, chatRoomId2))
        assert(room.isEmpty())
    }

    private fun generateChatRoomId(): ChatRoomId {
        return ChatRoomId.of(UUID.randomUUID().toString())
    }
}
