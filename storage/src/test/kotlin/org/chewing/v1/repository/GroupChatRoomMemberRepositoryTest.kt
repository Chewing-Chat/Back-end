package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jpaentity.chat.ChatRoomMemberId
import org.chewing.v1.jparepository.chat.GroupChatRoomMemberJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.chat.GroupChatRoomMemberRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class GroupChatRoomMemberRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var groupChatRoomMemberJpaRepository: GroupChatRoomMemberJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var groupChatRoomMemberRepositoryImpl: GroupChatRoomMemberRepositoryImpl

    @Test
    fun `그룹 채팅방 멤버 추가`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        groupChatRoomMemberRepositoryImpl.append(chatRoomId, userId)
        val member = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId))
        assert(member.isPresent)
    }

    @Test
    fun `그룹 채팅방 맴버 추가시 존재 한다면 delete 에서 normal로 변경`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId)
        groupChatRoomMemberRepositoryImpl.remove(chatRoomId, userId)
        groupChatRoomMemberRepositoryImpl.append(chatRoomId, userId)
        val member = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId))
        assert(member.isPresent)
        val memberInfo = member.get().toChatRoomMember()
        assert(memberInfo.status == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `그룹 채팅방 멤버 삭제`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId)
        groupChatRoomMemberRepositoryImpl.remove(chatRoomId, userId)
        val member = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId))
        assert(member.isPresent)
        val memberInfo = member.get().toChatRoomMember()
        assert(memberInfo.status == ChatRoomMemberStatus.DELETED)
    }

    @Test
    fun `그룹 채팅방 멤버 조회(삭제된 맴버는 제외)`() {
        val chatRoomId = generateChatRoomId()
        val userId1 = generateUserId()
        val userId2 = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId1)
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId2)
        groupChatRoomMemberRepositoryImpl.remove(chatRoomId, userId2)
        val member = groupChatRoomMemberRepositoryImpl.read(chatRoomId)
        assert(member.size == 1)
    }

    @Test
    fun `유저가 포함된 모든 채팅방 읽기`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId1, userId)
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId2, userId)
        val member = groupChatRoomMemberRepositoryImpl.readUsers(userId)
        assert(member.size == 2)
    }

    @Test
    fun `그룹 채팅방 맴버 상태 변경`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId)
        groupChatRoomMemberRepositoryImpl.updateStatus(chatRoomId, userId, ChatRoomMemberStatus.FAVORITE)
        val member = groupChatRoomMemberJpaRepository.findById(ChatRoomMemberId.of(chatRoomId, userId))
        assert(member.isPresent)
        val memberInfo = member.get().toChatRoomMember()
        assert(memberInfo.status == ChatRoomMemberStatus.FAVORITE)
    }

    @Test
    fun `참여자인지 확인 - 성공`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId)
        val result = groupChatRoomMemberRepositoryImpl.checkParticipant(chatRoomId, userId)
        assert(result)
    }

    @Test
    fun `참여자인지 확인 - 실패`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val result = groupChatRoomMemberRepositoryImpl.checkParticipant(chatRoomId, userId)
        assert(!result)
    }

    @Test
    fun `참여자인지 확인 - 실패 - 삭제된 참여자`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId, userId)
        groupChatRoomMemberRepositoryImpl.remove(chatRoomId, userId)
        val result = groupChatRoomMemberRepositoryImpl.checkParticipant(chatRoomId, userId)
        assert(!result)
    }

    @Test
    fun `채팅방 ID 리스트를 통해 모든 해당 모든 정보 가져옴`() {
        val chatRoomId1 = generateChatRoomId()
        val chatRoomId2 = generateChatRoomId()
        val userId = generateUserId()
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId1, userId)
        jpaDataGenerator.groupChatRoomMemberEntityData(chatRoomId2, userId)
        val result = groupChatRoomMemberRepositoryImpl.readsInfos(listOf(chatRoomId1, chatRoomId2))
        assert(result.size == 2)
    }

    private fun generateChatRoomId(): ChatRoomId {
        return ChatRoomId.of(UUID.randomUUID().toString())
    }

    private fun generateUserId(): UserId {
        return UserId.of(UUID.randomUUID().toString())
    }
}
