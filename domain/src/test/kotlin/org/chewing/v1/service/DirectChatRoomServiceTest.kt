package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.mockk
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomAppender
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomEnricher
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomReader
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomRemover
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomUpdater
import org.chewing.v1.implementation.chat.directroom.DirectChatRoomValidator
import org.chewing.v1.implementation.chat.sequence.ChatSequenceFinder
import org.chewing.v1.implementation.chat.sequence.ChatSequenceHandler
import org.chewing.v1.repository.chat.ChatRoomMemberSequenceRepository
import org.chewing.v1.repository.chat.ChatRoomSequenceRepository
import org.chewing.v1.repository.chat.DirectChatRoomRepository
import org.chewing.v1.service.chat.DirectChatRoomService
import io.mockk.every
import io.mockk.just
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DirectChatRoomServiceTest {
    private val directChatRoomRepository: DirectChatRoomRepository = mockk()
    private val chatRoomMemberSequenceRepository: ChatRoomMemberSequenceRepository = mockk()
    private val chatRoomSequenceRepository: ChatRoomSequenceRepository = mockk()

    private val directChatRoomReader = DirectChatRoomReader(directChatRoomRepository)
    private val directChatRoomRemover = DirectChatRoomRemover(directChatRoomRepository)
    private val directChatRoomUpdater = DirectChatRoomUpdater(directChatRoomRepository)
    private val directChatRoomValidator = DirectChatRoomValidator()
    private val directChatRoomAppender = DirectChatRoomAppender(directChatRoomRepository)
    private val chatSequenceFinder = ChatSequenceFinder(chatRoomMemberSequenceRepository, chatRoomSequenceRepository)
    private val chatSequenceHandler = ChatSequenceHandler(chatRoomMemberSequenceRepository, chatRoomSequenceRepository)
    private val directChatRoomEnricher = DirectChatRoomEnricher()
    private val directChatRoomService = DirectChatRoomService(
        directChatRoomReader,
        directChatRoomRemover,
        directChatRoomUpdater,
        directChatRoomValidator,
        directChatRoomAppender,
        chatSequenceFinder,
        chatSequenceHandler,
        directChatRoomEnricher,
    )

    @Test
    fun `채팅방 생성 - 이미 채팅방 이 존재`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val chatRoomUserStatus = ChatRoomMemberStatus.NORMAL
        val chatRoomFriendStatus = ChatRoomMemberStatus.NORMAL
        val chatRoomInfo = TestDataFactory.createDirectChatRoomInfo(chatRoomId, userId, friendId, chatRoomUserStatus, chatRoomFriendStatus)

        every { directChatRoomRepository.readWithRelation(userId, friendId) } returns chatRoomInfo

        assertDoesNotThrow {
            directChatRoomService.createDirectChatRoom(userId, friendId)
        }

        verify(exactly = 1) { directChatRoomRepository.readWithRelation(userId, friendId) }
        verify(exactly = 0) { directChatRoomRepository.append(any(), any()) }
        verify(exactly = 0) { directChatRoomRepository.updateStatus(any(), any(), any()) }
    }

    @Test
    fun `채팅방 생성 - 생성된 적이 없다면 생성`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val chatRoomUserStatus = ChatRoomMemberStatus.NORMAL
        val chatRoomFriendStatus = ChatRoomMemberStatus.NORMAL
        val newChatRoomInfo = TestDataFactory.createDirectChatRoomInfo(chatRoomId, userId, friendId, chatRoomUserStatus, chatRoomFriendStatus)

        every { directChatRoomRepository.readWithRelation(userId, friendId) } returns null
        every { directChatRoomRepository.append(userId, friendId) } returns newChatRoomInfo
        every { chatRoomSequenceRepository.appendSequence(chatRoomId) } just Runs
        every { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, any()) } just Runs

        val result = assertDoesNotThrow {
            directChatRoomService.createDirectChatRoom(userId, friendId)
        }

        verify(exactly = 1) { directChatRoomRepository.append(userId, friendId) }
        verify(exactly = 1) { chatRoomSequenceRepository.appendSequence(chatRoomId) }
        verify(exactly = 1) { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, friendId) }
        verify(exactly = 1) { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, userId) }
        assert(result == chatRoomId)
    }

    @Test
    fun `채팅방 생성 - 삭제된 채팅방이 존재한다면 복구`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val chatRoomId = TestDataFactory.createChatRoomId()
        val chatRoomUserStatus = ChatRoomMemberStatus.DELETED
        val chatRoomFriendStatus = ChatRoomMemberStatus.NORMAL
        val chatRoomInfo = TestDataFactory.createDirectChatRoomInfo(chatRoomId, userId, friendId, chatRoomUserStatus, chatRoomFriendStatus)
        val chatRoomSequence = TestDataFactory.createChatRoomSequence(chatRoomId)

        every { directChatRoomRepository.readWithRelation(userId, friendId) } returns chatRoomInfo
        every { chatRoomSequenceRepository.appendSequence(chatRoomId) } just Runs
        every { chatRoomMemberSequenceRepository.appendSequence(chatRoomId, any()) } just Runs
        every { directChatRoomRepository.updateStatus(userId, chatRoomId, ChatRoomMemberStatus.NORMAL) } just Runs
        every { chatRoomSequenceRepository.readSequence(chatRoomId) } returns chatRoomSequence
        every { chatRoomMemberSequenceRepository.updateJoinSequence(chatRoomId, userId, chatRoomSequence) } returns TestDataFactory.createChatRoomMemberSequence(chatRoomId)
        val result = assertDoesNotThrow {
            directChatRoomService.createDirectChatRoom(userId, friendId)
        }

        verify(exactly = 1) { directChatRoomRepository.updateStatus(userId, chatRoomId, ChatRoomMemberStatus.NORMAL) }
        assert(result == chatRoomId)
    }
}
