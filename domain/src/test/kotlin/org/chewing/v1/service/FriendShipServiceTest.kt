package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.friend.friendship.*
import org.chewing.v1.model.friend.FriendSortCriteria
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.friend.FriendShipRepository
import org.chewing.v1.service.friend.FriendShipService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class FriendShipServiceTest {
    private val friendShipRepository: FriendShipRepository = mockk()
    private val friendShipReader = FriendShipReader(friendShipRepository)
    private val friendShipRemover = FriendShipRemover(friendShipRepository)
    private val friendShipAppender = FriendShipAppender(friendShipRepository)
    private val friendShipValidator = FriendShipValidator(friendShipRepository)
    private val friendShipUpdater = FriendShipUpdater(friendShipRepository)
    private val friendShipService = FriendShipService(
        friendShipReader,
        friendShipRemover,
        friendShipAppender,
        friendShipValidator,
        friendShipUpdater,
    )

    @Test
    fun `접근 가능한 친구 조회`() {
        val userId = "userId"
        val friendId = "friendId"
        val sort = FriendSortCriteria.NAME
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.readsAccess(userId, AccessStatus.ACCESS, sort) } returns listOf(friendShip)

        val result = assertDoesNotThrow {
            friendShipService.getAccessFriendShips(userId, sort)
        }
        assert(result.size == 1)
    }

    @Test
    fun `유저의 접근 가능한 친구 관계 정보를 친구 아이디를 통해 조회한다`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendIds = listOf(friendId)
        val friendShips = listOf(TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS))

        every { friendShipRepository.reads(friendIds, userId, AccessStatus.ACCESS) } returns friendShips

        val result = assertDoesNotThrow {
            friendShipService.getAccessFriendShipsIn(friendIds, userId)
        }

        assert(result.size == 1)
    }

    @Test
    fun `친구 추가 실패 - 자기 자신을 추가 할 수 없음`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()
        val friendId = "userId"
        val friendName = TestDataFactory.createUserName()

        val exception = assertThrows<ConflictException> {
            friendShipService.createFriendShip(userId, userName, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_MYSELF)
    }

    @Test
    fun `친구 추가 실패 - 내가 차단한 친구이다`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCK)

        every { friendShipRepository.read(userId, friendId) } returns friendShip

        val exception = assertThrows<ConflictException> {
            friendShipService.createFriendShip(userId, userName, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
    }

    @Test
    fun `친구 추가 실패 - 내가 차단당한 친구이다`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCKED)

        every { friendShipRepository.read(userId, friendId) } returns friendShip

        val exception = assertThrows<ConflictException> {
            friendShipService.createFriendShip(userId, userName, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
    }

    @Test
    fun `친구 추가 실패 - 이미 친구관계를 맺었다`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip

        val exception = assertThrows<ConflictException> {
            friendShipService.createFriendShip(userId, userName, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_ALREADY_CREATED)
    }

    @Test
    fun `친구 추가 성공`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()

        every { friendShipRepository.read(userId, friendId) } returns null
        every { friendShipRepository.append(userId, friendId, friendName) } just Runs
        every { friendShipRepository.append(friendId, userId, userName) } just Runs

        assertDoesNotThrow {
            friendShipService.createFriendShip(userId, userName, friendId, friendName)
        }
    }

    @Test
    fun `친구 삭제 성공`() {
        val userId = "userId"
        val friendId = "friendId"

        every { friendShipRepository.remove(userId, friendId) } returns userId
        every { friendShipRepository.remove(friendId, userId) } returns friendId

        assertDoesNotThrow {
            friendShipService.removeFriendShip(userId, friendId)
        }
    }

    @Test
    fun `친구 삭제 실패 - 친구 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.remove(userId, friendId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.removeFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 삭제 실패 - 친구 입장에서 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.remove(userId, friendId) } returns userId
        every { friendShipRepository.remove(friendId, userId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.removeFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 차단 성공`() {
        val userId = "userId"
        val friendId = "friendId"

        every { friendShipRepository.block(userId, friendId) } returns userId
        every { friendShipRepository.blocked(friendId, userId) } returns friendId

        assertDoesNotThrow {
            friendShipService.blockFriendShip(userId, friendId)
        }
    }

    @Test
    fun `친구 차단 실패 - 친구 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.block(userId, friendId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.blockFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 차단 실패 - 친구 입장에서 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"

        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.block(userId, friendId) } returns userId
        every { friendShipRepository.blocked(friendId, userId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.blockFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 즐겨 찾기 설정 성공`() {
        val userId = "userId"
        val friendId = "friendId"
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        assertDoesNotThrow {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구를 차단함`() {
        val userId = "userId"
        val friendId = "friendId"
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCK)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구가 차단함`() {
        val userId = "userId"
        val friendId = "friendId"
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCKED)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 이름 수정 성공`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        assertDoesNotThrow {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }
    }

    @Test
    fun `친구 이름 수정 실패 - 친구를 차단함`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCK)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
    }

    @Test
    fun `친구 이름 수정 실패 - 친구가 차단함`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.BLOCKED)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
    }

    @Test
    fun `친구 이름 수정 실패 - 친구 관계가 존재하지 않음`() {
        val userId = "userId"
        val friendId = "friendId"
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }
}
