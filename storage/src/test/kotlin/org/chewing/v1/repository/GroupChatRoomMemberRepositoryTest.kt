package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.room.ChatNumber
import org.chewing.v1.repository.jpa.chat.GroupChatRoomMemberRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class GroupChatRoomMemberRepositoryTest : JpaContextTest() {

    @Autowired
    private lateinit var groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var chatRoomMemberRepositoryImpl: GroupChatRoomMemberRepositoryImpl

    @Test
    fun `채팅방 유저 목록을 가져와야함`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()
        val number = ChatNumber.of(chatRoomId, 1, 1)
        jpaDataGenerator.groupChatRoomMemberEntityDataList(chatRoomId, listOf(userId, friendId, friendId2), number)
        val chatRoomMemberInfos = chatRoomMemberRepositoryImpl.readFriends(chatRoomId, userId)
        assert(chatRoomMemberInfos.size == 2)
    }

    @Test
    fun `채팅방에 유저들을 추가해야함`() {
        val chatRoomId = generateChatRoomId()
        val userIds = generateUserIds()
        val number = ChatNumber.of(chatRoomId, 1, 1)
        chatRoomMemberRepositoryImpl.appends(chatRoomId, userIds, number)
        assert(groupChatRoomMemberJpaRepository.findByIdChatRoomIdIn(listOf(chatRoomId)).size == 2)
    }

    @Test
    fun `채팅방에서 좋아요 변경 처리`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val number = ChatNumber.of(chatRoomId, 1, 1)

        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId, number)
        chatRoomMemberRepositoryImpl.updateFavorite(chatRoomId, userId, true)
        val result = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(chatRoomId, userId))
        assert(result.isPresent)
        assert(result.get().toRoomMember().favorite)
    }

    @Test
    fun `채팅방에서 유저 읽음 처리`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val preChatNumber = ChatNumber.of(chatRoomId, 1, 0)
        val chatNumber = ChatNumber.of(chatRoomId, 50, 1)
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId, preChatNumber)

        chatRoomMemberRepositoryImpl.updateRead(userId, chatNumber)

        val result = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(chatRoomId, userId))
        assert(result.isPresent)
        assert(result.get().toRoomMember().readSeqNumber == chatNumber.sequenceNumber)
    }

    @Test
    fun `채팅방에서 유저 삭제 처리`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val chatNumber = ChatNumber.of(chatRoomId, 50, 1)

        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId, chatNumber)
        chatRoomMemberRepositoryImpl.removes(listOf(chatRoomId), userId)

        val result = groupChatRoomMemberJpaRepository.findAllByIdUserId(userId)
        assert(result.isEmpty())
    }

    @Test
    fun `채팅방 유저를 추가해야함`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val chatNumber = ChatNumber.of(chatRoomId, 50, 1)

        chatRoomMemberRepositoryImpl.append(chatRoomId, userId, chatNumber)
        val result = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(chatRoomId, userId))
        assert(result.isPresent)
        assert(result.get().toRoomMember().chatRoomId == chatRoomId)
        assert(result.get().toRoomMember().memberId == userId)
        assert(result.get().toRoomMember().readSeqNumber == chatNumber.sequenceNumber)
    }

    @Test
    fun `채팅방 유저들을 추가해야함`() {
        val chatRoomId = generateChatRoomId()
        val userIds = generateUserIds()
        val chatNumber = ChatNumber.of(chatRoomId, 50, 1)

        chatRoomMemberRepositoryImpl.appends(chatRoomId, userIds, chatNumber)
        val results = userIds.map { groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId(chatRoomId, it)) }
        assert(results.all { it.isPresent })
    }

    @Test
    fun `채팅방 유저 친구들을 가져와야함`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val friendId = generateUserId()
        val friendId2 = generateUserId()
        val number = ChatNumber.of(chatRoomId, 1, 1)
        jpaDataGenerator.groupChatRoomMemberEntityDataList(chatRoomId, listOf(userId, friendId, friendId2), number)
        val chatRoomMemberInfo = chatRoomMemberRepositoryImpl.readFriends(chatRoomId, userId)
        assert(chatRoomMemberInfo.size == 2)
    }

    private fun generateUserId() = UUID.randomUUID().toString()
    private fun generateUserIds() = listOf(generateUserId(), generateUserId())
    private fun generateChatRoomId() = UUID.randomUUID().toString()
}
