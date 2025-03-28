package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomAppender
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomEnricher
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomReader
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomRemover
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomUpdater
import org.chewing.v1.implementation.chat.grouproom.GroupChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.chat.room.GroupChatRoomMemberInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.chewing.v1.repository.chat.GroupChatRoomMemberRepository
import org.chewing.v1.repository.chat.GroupChatRoomRepository
import org.chewing.v1.service.chat.GroupChatRoomService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class GroupChatRoomServiceTest {
    private val groupChatRoomRepository: GroupChatRoomRepository = mockk()
    private val groupChatRoomMemberRepository: GroupChatRoomMemberRepository = mockk()
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository = mockk()
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository = mockk()

    private val groupChatRoomAppender = GroupChatRoomAppender(groupChatRoomRepository, groupChatRoomMemberRepository)
    private val groupChatRoomReader = GroupChatRoomReader(groupChatRoomRepository, groupChatRoomMemberRepository)
    private val groupChatRoomRemover = GroupChatRoomRemover(groupChatRoomMemberRepository)
    private val groupChatRoomUpdater = GroupChatRoomUpdater(groupChatRoomMemberRepository, groupChatRoomRepository)
    private val groupChatRoomValidator = GroupChatRoomValidator(groupChatRoomMemberRepository)
    private val chatSequenceFinder = ChatSequenceFinder(chatRoomMemberSequenceRepository, chatRoomSequenceRepository)
    private val chatSequenceHandler = ChatSequenceHandler(chatRoomMemberSequenceRepository, chatRoomSequenceRepository)
    private val groupChatRoomEnricher = GroupChatRoomEnricher()

    private val groupChatRoomService = GroupChatRoomService(
        groupChatRoomAppender,
        groupChatRoomReader,
        groupChatRoomRemover,
        groupChatRoomUpdater,
        groupChatRoomValidator,
        chatSequenceFinder,
        chatSequenceHandler,
        groupChatRoomEnricher,
    )

    @Test
    fun `그룹 채팅방 제공`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val memberIds = (friendIds + userId)
        val groupName = "groupName"

        every { groupChatRoomRepository.append(groupName) } returns chatRoomId
        every { groupChatRoomMemberRepository.append(chatRoomId, any()) } just Runs
        every { chatRoomSequenceRepository.appendSequence(chatRoomId) } just Runs
        every { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, any()) } just Runs

        val result = groupChatRoomService.produceGroupChatRoom(userId, friendIds, groupName)

        assert(result == chatRoomId)
        verify(exactly = memberIds.size) { groupChatRoomMemberRepository.append(chatRoomId, any()) }
        verify(exactly = memberIds.size) { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, any()) }
    }

    @Test
    fun `그룹 채팅방 삭제`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomId = TestDataFactory.createChatRoomId()

        every { groupChatRoomMemberRepository.checkParticipant(chatRoomId, userId) } returns true
        every { groupChatRoomRemover.removeMember(chatRoomId, userId) } just Runs

        assertDoesNotThrow {
            groupChatRoomService.deleteGroupChatRoom(userId, chatRoomId)
        }

        verify { groupChatRoomRemover.removeMember(chatRoomId, userId) }
    }

    @Test
    fun `그룹 채팅방 초대`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val friendId = TestDataFactory.createUserId()
        val chatRoomSequence = TestDataFactory.createChatRoomSequence(chatRoomId)
        val chatRoomMemberSequence = TestDataFactory.createChatRoomMemberSequence(chatRoomId)

        every { groupChatRoomMemberRepository.checkParticipant(chatRoomId, userId) } returns true
        every { groupChatRoomAppender.appendMember(chatRoomId, friendId) } just Runs
        every { chatRoomSequenceRepository.readSequence(chatRoomId) } returns chatRoomSequence
        every { chatRoomSequenceRepository.appendSequence(chatRoomId) } just Runs
        every { chatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, friendId, chatRoomSequence) } returns chatRoomMemberSequence

        assertDoesNotThrow {
            groupChatRoomService.inviteGroupChatRoom(userId, chatRoomId, friendId)
        }
    }

    @Test
    fun `그룹 채팅방 참여자 상태 변경`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val status = ChatRoomMemberStatus.FAVORITE

        every { groupChatRoomMemberRepository.checkParticipant(chatRoomId, userId) } returns true
        every { groupChatRoomUpdater.updateMemberStatus(chatRoomId, userId, status) } just Runs

        assertDoesNotThrow {
            groupChatRoomService.favoriteGroupChatRoomType(userId, chatRoomId, status)
        }

        verify { groupChatRoomUpdater.updateMemberStatus(chatRoomId, userId, status) }
    }

    @Test
    fun `그룹 채팅방 참여자 확인`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomId = TestDataFactory.createChatRoomId()

        every { groupChatRoomMemberRepository.checkParticipant(chatRoomId, userId) } returns true
        assertDoesNotThrow {
            groupChatRoomService.validateIsParticipant(chatRoomId, userId)
        }
    }

    @Test
    fun `유저가 포함된 그룹 채팅방 목록 가져오기`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val memberIds = (friendIds + userId)
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, memberIds)
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.getGroupChatRooms(userId)

        assert(result.size == chatRoomIds.size)
        result.forEachIndexed { index, groupChatRoom ->
            val chatRoom = chatRooms[index]
            val chatRoomSequence = chatRoomSequences[index]
            val memberSequence = userSequences[index]
            val chatRoomMemberInfo = chatRoomMemberInfos.filter { it.chatRoomId == chatRoom.chatRoomId }

            assert(groupChatRoom.roomInfo.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomInfo.name == chatRoom.name)
            assert(groupChatRoom.roomSequence.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomSequence.sequence == chatRoomSequence.sequence)
            assert(groupChatRoom.memberInfos.size == chatRoomMemberInfo.size)
            assert(groupChatRoom.ownSequence == memberSequence)
        }
    }

    @Test
    fun `친구 아이디 리스트를 통해 조회`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val memberIds = (friendIds + userId)
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, memberIds)
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.searchGroupChatRooms(userId, friendIds)

        assert(result.size == chatRoomIds.size)
        result.forEachIndexed { index, groupChatRoom ->
            val chatRoom = chatRooms[index]
            val chatRoomSequence = chatRoomSequences[index]
            val memberSequence = userSequences[index]
            val chatRoomMemberInfo = chatRoomMemberInfos.filter { it.chatRoomId == chatRoom.chatRoomId }

            assert(groupChatRoom.roomInfo.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomInfo.name == chatRoom.name)
            assert(groupChatRoom.roomSequence.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomSequence.sequence == chatRoomSequence.sequence)
            assert(groupChatRoom.memberInfos.size == chatRoomMemberInfo.size)
            assert(groupChatRoom.ownSequence == memberSequence)
        }
    }

    @Test
    fun `친구 아이디 리스트가 비어있을 때 빈 리스트 반환`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = emptyList<UserId>()
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, listOf(userId))
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.searchGroupChatRooms(userId, friendIds)

        assert(result.isEmpty())
    }

    @Test
    fun `유저가 참여한 방 중 친구가 포함된 방이 없을 때 빈 리스트 반환`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, listOf(userId))
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.searchGroupChatRooms(userId, friendIds)

        assert(result.isEmpty())
    }

    @Test
    fun `친구는 있지만, 해당 친구들이 아무 방에도 참여하지 않았을 때 빈 리스트 반환`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = emptyList<GroupChatRoomMemberInfo>()
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.searchGroupChatRooms(userId, friendIds)

        assert(result.isEmpty())
    }

    @Test
    fun `읽지 않은 채팅방이 있을 경우 반환`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, listOf(userId))
        val chatRoomSequences = TestDataFactory.createChatRoomSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.getUnreadGroupChatRooms(userId)

        assert(result.size == chatRoomIds.size)
        result.forEachIndexed { index, groupChatRoom ->
            val chatRoom = chatRooms[index]
            val chatRoomSequence = chatRoomSequences[index]
            val memberSequence = userSequences[index]
            val chatRoomMemberInfo = chatRoomMemberInfos.filter { it.chatRoomId == chatRoom.chatRoomId }

            assert(groupChatRoom.roomInfo.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomInfo.name == chatRoom.name)
            assert(groupChatRoom.roomSequence.chatRoomId == chatRoom.chatRoomId)
            assert(groupChatRoom.roomSequence.sequence == chatRoomSequence.sequence)
            assert(groupChatRoom.memberInfos.size == chatRoomMemberInfo.size)
            assert(groupChatRoom.ownSequence == memberSequence)
        }
    }

    @Test
    fun `읽지 않은 채팅방이 없을 경우 빈 리스트 반환`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomIds = TestDataFactory.createChatRoomIds()
        val chatRooms = TestDataFactory.createGroupChatRoomInfos(chatRoomIds)
        val chatRoomUsersInfos = TestDataFactory.createGroupChatRoomUserInfos(chatRoomIds, userId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMembersInfos(chatRoomIds, listOf(userId))
        val chatRoomSequences = TestDataFactory.createChatRoomUnReadSequences(chatRoomIds)
        val userSequences = TestDataFactory.createChatRoomMemberSequences(chatRoomIds)

        every { groupChatRoomMemberRepository.readUsers(userId) } returns chatRoomUsersInfos
        every { groupChatRoomRepository.readRoomInfos(chatRoomIds) } returns chatRooms
        every { groupChatRoomMemberRepository.readsAllInfos(chatRoomIds) } returns chatRoomMemberInfos
        every { chatRoomSequenceRepository.readsSequences(chatRoomIds) } returns chatRoomSequences
        every { chatRoomMemberSequenceRepository.readsSequences(chatRoomIds, userId) } returns userSequences

        val result = groupChatRoomService.getUnreadGroupChatRooms(userId)

        assert(result.isEmpty())
    }

    @Test
    fun `그룹 채팅방 조회`() {
        val userId = TestDataFactory.createUserId()
        val friendIds = TestDataFactory.createFriendIds()
        val memberIds = (friendIds + userId)

        val chatRoomId = TestDataFactory.createChatRoomId()
        val chatRoom = TestDataFactory.createGroupChatRoomInfo(chatRoomId)
        val chatRoomSequence = TestDataFactory.createChatRoomSequence(chatRoomId)
        val chatRoomMemberSequence = TestDataFactory.createChatRoomMemberSequence(chatRoomId)
        val chatRoomMemberInfos = TestDataFactory.createGroupChatRoomMemberInfos(chatRoomId, memberIds)

        every { groupChatRoomRepository.readRoomInfo(chatRoomId) } returns chatRoom
        every { chatRoomSequenceRepository.readSequence(chatRoomId) } returns chatRoomSequence
        every { chatRoomMemberSequenceRepository.readSequence(chatRoomId, userId) } returns chatRoomMemberSequence
        every { groupChatRoomMemberRepository.read(chatRoomId) } returns chatRoomMemberInfos

        val result = groupChatRoomService.getGroupChatRoom(userId, chatRoomId)

        assert(result.roomInfo == chatRoom)
        assert(result.roomSequence == chatRoomSequence)
        assert(result.ownSequence == chatRoomMemberSequence)
        assert(result.memberInfos == chatRoomMemberInfos)
    }

    @Test
    fun `그룹 채팅방 읽음 처리`() {
        val userId = TestDataFactory.createUserId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val sequenceNumber = 1
        val chatRoomMemberSequence = TestDataFactory.createChatRoomMemberSequence(chatRoomId)

        every { chatRoomMemberSequenceRepository.updateReadSequence(chatRoomId, userId, sequenceNumber) } returns chatRoomMemberSequence

        assertDoesNotThrow {
            groupChatRoomService.readGroupChatRoom(userId, chatRoomId, sequenceNumber)
        }
    }

    @Test
    fun `그룹 채팅방 시퀀스 증가`() {
        val chatRoomId = TestDataFactory.createChatRoomId()
        val chatRoomSequence = TestDataFactory.createChatRoomSequence(chatRoomId)

        every { chatRoomSequenceRepository.updateIncreaseSequence(chatRoomId) } returns chatRoomSequence

        val result = groupChatRoomService.increaseGroupChatRoomSequence(chatRoomId)

        assert(result == chatRoomSequence)
    }
}
