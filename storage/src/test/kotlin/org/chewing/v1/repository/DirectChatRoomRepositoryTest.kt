package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.chat.DirectChatRoomJpaRepository
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomMemberStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.chat.DirectChatRoomRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class DirectChatRoomRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var directChatRoomJpaRepository: DirectChatRoomJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var directChatRoomRepositoryImpl: DirectChatRoomRepositoryImpl

    @Test
    fun `개인 채팅방 생성`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = directChatRoomRepositoryImpl.append(userId, friendId)
        val room = directChatRoomJpaRepository.findById(result.chatRoomId.id)
        assert(room.isPresent)
        val roomInfo = room.get().toChatRoom(userId)
        assert(roomInfo.userId == userId)
        assert(roomInfo.friendId == friendId)
        assert(roomInfo.chatRoomId == result.chatRoomId)
        assert(roomInfo.status == ChatRoomMemberStatus.NORMAL)
        assert(roomInfo.friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `개인 채팅방 조회`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        val room = directChatRoomRepositoryImpl.readInfo(result.chatRoomId, userId)
        assert(room != null)
        assert(room!!.userId == userId)
        assert(room.friendId == friendId)
        assert(room.chatRoomId == result.chatRoomId)
        assert(room.status == ChatRoomMemberStatus.NORMAL)
        assert(room.friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `개인 채팅방 조회 실패`() {
        val chatRoomId = generateChatRoomId()
        val userId = generateUserId()
        val room = directChatRoomRepositoryImpl.readInfo(chatRoomId, userId)
        assert(room == null)
    }

    @Test
    fun `친구 관계를 통해 채팅방 조회`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        val room = directChatRoomRepositoryImpl.readWithRelation(userId, friendId)
        assert(room != null)
        assert(room!!.userId == userId)
        assert(room.friendId == friendId)
        assert(room.chatRoomId == result.chatRoomId)
        assert(room.status == ChatRoomMemberStatus.NORMAL)
        assert(room.friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `자신이 삭제 하지 않은 모든 채팅방 조회`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        val deleteResult = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        directChatRoomRepositoryImpl.remove(userId, deleteResult.chatRoomId)
        val rooms = directChatRoomRepositoryImpl.readUsers(userId)
        assert(rooms.size == 1)
        assert(rooms[0].userId == userId)
        assert(rooms[0].friendId == friendId)
        assert(rooms[0].chatRoomId == result.chatRoomId)
        assert(rooms[0].status == ChatRoomMemberStatus.NORMAL)
        assert(rooms[0].friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `채팅방 상태 변경`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        directChatRoomRepositoryImpl.updateStatus(userId, result.chatRoomId, ChatRoomMemberStatus.FAVORITE)
        val room = directChatRoomJpaRepository.findById(result.chatRoomId.id)
        assert(room.isPresent)
        val roomInfo = room.get().toChatRoom(userId)
        assert(roomInfo.userId == userId)
        assert(roomInfo.friendId == friendId)
        assert(roomInfo.chatRoomId == result.chatRoomId)
        assert(roomInfo.status == ChatRoomMemberStatus.FAVORITE)
        assert(roomInfo.friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    @Test
    fun `채팅방 삭제`() {
        val userId = generateUserId()
        val friendId = generateUserId()
        val result = jpaDataGenerator.directChatRoomEntityData(userId, friendId)
        directChatRoomRepositoryImpl.remove(userId, result.chatRoomId)
        val room = directChatRoomJpaRepository.findById(result.chatRoomId.id)
        assert(room.isPresent)
        val roomInfo = room.get().toChatRoom(userId)
        assert(roomInfo.userId == userId)
        assert(roomInfo.friendId == friendId)
        assert(roomInfo.chatRoomId == result.chatRoomId)
        assert(roomInfo.status == ChatRoomMemberStatus.DELETED)
        assert(roomInfo.friendStatus == ChatRoomMemberStatus.NORMAL)
    }

    fun generateUserId() = UserId.of(UUID.randomUUID().toString())

    fun generateChatRoomId() = ChatRoomId.of(UUID.randomUUID().toString())
}
