package org.chewing.v1.service

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.friend.friendship.*
import org.chewing.v1.model.friend.FriendShipStatus
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
    private val friendShipValidator = FriendShipValidator()
    private val friendShipUpdater = FriendShipUpdater(friendShipRepository)
    private val friendShipFilter = FriendShipFilter()
    private val friendShipService = FriendShipService(
        friendShipReader,
        friendShipRemover,
        friendShipAppender,
        friendShipValidator,
        friendShipUpdater,
        friendShipFilter,
    )

    @Test
    fun `접근 가능한 친구 조회`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.reads(userId) } returns listOf(friendShip)

        val result = assertDoesNotThrow {
            friendShipService.getFriendShips(userId)
        }
        assert(result.size == 1)
    }

//
//    @Test
//    fun `친구 추가 실패 - 자기 자신을 추가 할 수 없음`() {
//        val userId = TestDataFactory.createUserId()
//        val userName = TestDataFactory.createUserName()
//        val friendId = TestDataFactory.createUserId()
//        val friendName = TestDataFactory.createUserName()
//
//        val exception = assertThrows<ConflictException> {
//            friendShipService.createFriendShips(userId, userName, friendId, friendName)
//        }
//
//        assert(exception.errorCode == ErrorCode.FRIEND_MYSELF)
//    }
//
//    @Test
//    fun `친구 추가 실패 - 내가 차단한 친구이다`() {
//        val userId = TestDataFactory.createUserId()
//        val userName = TestDataFactory.createUserName()
//        val friendId = TestDataFactory.createFriendId()
//        val friendName = TestDataFactory.createUserName()
//        val friendShip = TestDataFactory.createFriendShip(friendId, FriendShipStatus.BLOCK)
//
//        every { friendShipRepository.read(userId, friendId) } returns friendShip
//
//        val exception = assertThrows<ConflictException> {
//            friendShipService.createFriendShips(userId, userName, friendId, friendName)
//        }
//
//        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
//    }
//
//    @Test
//    fun `친구 추가 실패 - 내가 차단당한 친구이다`() {
//        val userId = TestDataFactory.createUserId()
//        val userName = TestDataFactory.createUserName()
//        val friendId = TestDataFactory.createFriendId()
//        val friendName = TestDataFactory.createUserName()
//        val friendShip = TestDataFactory.createFriendShip(friendId, FriendShipStatus.BLOCKED)
//
//        every { friendShipRepository.read(userId, friendId) } returns friendShip
//
//        val exception = assertThrows<ConflictException> {
//            friendShipService.createFriendShips(userId, userName, friendId, friendName)
//        }
//
//        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
//    }
//
//    @Test
//    fun `친구 추가 실패 - 이미 친구관계를 맺었다`() {
//        val userId = TestDataFactory.createUserId()
//        val userName = TestDataFactory.createUserName()
//        val friendId = TestDataFactory.createFriendId()
//        val friendName = TestDataFactory.createUserName()
//        val friendShip = TestDataFactory.createFriendShip(friendId, AccessStatus.ACCESS)
//
//        every { friendShipRepository.read(userId, friendId) } returns friendShip
//
//        val exception = assertThrows<ConflictException> {
//            friendShipService.createFriendShips(userId, userName, friendId, friendName)
//        }
//
//        assert(exception.errorCode == ErrorCode.FRIEND_ALREADY_CREATED)
//    }
//
//    @Test
//    fun `친구 추가 성공`() {
//        val userId = TestDataFactory.createUserId()
//        val userName = TestDataFactory.createUserName()
//        val friendId = TestDataFactory.createFriendId()
//        val friendName = TestDataFactory.createUserName()
//
//        every { friendShipRepository.read(userId, friendId) } returns null
//        every { friendShipRepository.append(userId, friendId, friendName) } just Runs
//        every { friendShipRepository.append(friendId, userId, userName) } just Runs
//
//        assertDoesNotThrow {
//            friendShipService.createFriendShips(userId, userName, friendId, friendName)
//        }
//    }

    @Test
    fun `친구 삭제 성공`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()

        every { friendShipRepository.remove(userId, friendId) } returns userId
        every { friendShipRepository.remove(friendId, userId) } returns friendId

        assertDoesNotThrow {
            friendShipService.removeFriendShip(userId, friendId)
        }
    }

    @Test
    fun `친구 삭제 실패 - 친구 관계가 존재하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.remove(userId, friendId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.removeFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 차단 성공`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()

        every { friendShipRepository.block(userId, friendId) } returns userId
        every { friendShipRepository.blocked(friendId, userId) } returns friendId

        assertDoesNotThrow {
            friendShipService.blockFriendShip(userId, friendId)
        }
    }

    @Test
    fun `친구 차단 실패 - 친구 관계가 존재하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()

        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.block(userId, friendId) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.blockFriendShip(userId, friendId)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 차단 실패 - 친구 입장에서 관계가 존재하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()

        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

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
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        assertDoesNotThrow {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구를 차단함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.BLOCK)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구가 차단함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.BLOCKED)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
    }

    @Test
    fun `친구 즐겨 찾기 설정 실패 - 친구 관계가 존재하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val favorite = true
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateFavorite(userId, friendId, favorite) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.changeFriendFavorite(userId, friendId, favorite)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }

    @Test
    fun `친구 이름 수정 성공`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        assertDoesNotThrow {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }
    }

    @Test
    fun `친구 이름 수정 실패 - 친구를 차단함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.BLOCK)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCK)
    }

    @Test
    fun `친구 이름 수정 실패 - 친구가 차단함`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.BLOCKED)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns userId

        val exception = assertThrows<ConflictException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_BLOCKED)
    }

    @Test
    fun `친구 이름 수정 실패 - 친구 관계가 존재하지 않음`() {
        val userId = TestDataFactory.createUserId()
        val friendId = TestDataFactory.createFriendId()
        val friendName = TestDataFactory.createUserName()
        val friendShip = TestDataFactory.createFriendShip(userId, friendId, FriendShipStatus.FRIEND)

        every { friendShipRepository.read(userId, friendId) } returns friendShip
        every { friendShipRepository.updateName(userId, friendId, friendName) } returns null

        val exception = assertThrows<NotFoundException> {
            friendShipService.changeFriendName(userId, friendId, friendName)
        }

        assert(exception.errorCode == ErrorCode.FRIEND_NOT_FOUND)
    }
}
