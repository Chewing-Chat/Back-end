package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.chat.ChatRoomJpaRepository
import org.chewing.v1.repository.jpa.chat.ChatRoomRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

internal class ChatRoomRepositoryTest : JpaContextTest() {

    @Autowired
    private lateinit var chatRoomJpaRepository: ChatRoomJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var chatRoomRepositoryImpl: ChatRoomRepositoryImpl

    @Test
    fun `그룹 채팅방을 추가해야 한다`() {
        val isGroup = true
        val result = chatRoomRepositoryImpl.appendChatRoom(isGroup)
        assert(result.isNotEmpty())
    }

    @Test
    fun `개인 채탕방을 생성해야 한다`() {
        val isGroup = false
        val result = chatRoomRepositoryImpl.appendChatRoom(isGroup)
        assert(result.isNotEmpty())
    }

    @Test
    fun `채팅방 목록을 가져와야 한다`() {
        val personalChatRoomInfo = jpaDataGenerator.chatRoomEntityData(false)
        val groupChatRoomInfo = jpaDataGenerator.chatRoomEntityData(true)
        val result =
            chatRoomRepositoryImpl.readChatRooms(listOf(personalChatRoomInfo.chatRoomId, groupChatRoomInfo.chatRoomId))

        assert(result.size == 2)
        result.find {
            it.chatRoomId == personalChatRoomInfo.chatRoomId
        }!!.let {
            assert(!it.isGroup)
        }
        result.find {
            it.chatRoomId == groupChatRoomInfo.chatRoomId
        }!!.let {
            assert(it.isGroup)
        }
    }

    @Test
    fun `그룹 채팅방인지 확인해야 한다 - 그룹채팅방이여야함`() {
        val groupChatRoomInfo = jpaDataGenerator.chatRoomEntityData(true)
        val result = chatRoomRepositoryImpl.isGroupChatRoom(groupChatRoomInfo.chatRoomId)
        assert(result)
    }

    @Test
    fun `그룹 채팅방인지 확인해야 한다 - 개인채팅방이여야함`() {
        val personalChatRoomInfo = jpaDataGenerator.chatRoomEntityData(false)
        val result = chatRoomRepositoryImpl.isGroupChatRoom(personalChatRoomInfo.chatRoomId)
        assert(!result)
    }
}
