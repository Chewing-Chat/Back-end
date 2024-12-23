package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.implementation.user.status.UserStatusAppender
import org.chewing.v1.implementation.user.status.UserStatusReader
import org.chewing.v1.implementation.user.status.UserStatusRemover
import org.chewing.v1.implementation.user.status.UserStatusUpdater
import org.chewing.v1.repository.user.UserStatusRepository
import org.chewing.v1.service.user.UserStatusService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class UserStatusServiceTest {

    private val userStatusRepository: UserStatusRepository = mockk()

    private val userStatusReader: UserStatusReader = UserStatusReader(userStatusRepository)
    private val userStatusUpdater: UserStatusUpdater = UserStatusUpdater(userStatusRepository)
    private val userStatusRemover: UserStatusRemover = UserStatusRemover(userStatusRepository)
    private val userStatusAppender: UserStatusAppender = UserStatusAppender(userStatusRepository)

    private val userStatusService = UserStatusService(userStatusReader, userStatusUpdater, userStatusRemover, userStatusAppender)

    @Test
    fun `유저의 상태를 선택함 처리`() {
        val userId = "userId"
        val statusId = "statusId"

        every { userStatusRepository.updateSelectedStatusFalse(userId) } just Runs
        every { userStatusRepository.updateSelectedStatusTrue(userId, statusId) } just Runs

        assertDoesNotThrow {
            userStatusService.selectUserStatus(userId, statusId)
        }
    }

    @Test
    fun `유저의 상태를 선택 해제`() {
        val userId = "userId"

        every { userStatusRepository.updateSelectedStatusFalse(userId) } just Runs

        assertDoesNotThrow {
            userStatusService.deleteSelectUserStatus(userId)
        }
    }

    @Test
    fun `유저의 상태를 생성`() {
        val userId = "userId"
        val statusMessage = "statusMessage"
        val emoji = "emoji"

        every { userStatusRepository.append(userId, statusMessage, emoji) } just Runs

        assertDoesNotThrow {
            userStatusService.createUserStatus(userId, statusMessage, emoji)
        }
    }

    @Test
    fun `유저의 모든 상태를 가져온다`() {
        val userId = "userId"
        val userStatus = TestDataFactory.createUserStatus(userId)

        every { userStatusRepository.reads(userId) } returns listOf(userStatus)

        val result = assertDoesNotThrow {
            userStatusService.getUserStatuses(userId)
        }

        assert(result.size == 1)
    }

    @Test
    fun `유저들의 선택한 상태들을 가져온다`() {
        val userId = "userId"
        val userIds = listOf(userId)
        val userStatus = TestDataFactory.createUserStatus(userId)

        every { userStatusRepository.readSelectedUsers(userIds) } returns listOf(userStatus)

        val result = assertDoesNotThrow {
            userStatusService.getSelectedStatuses(userIds)
        }

        assert(result.size == 1)
    }

    @Test
    fun `유저의 모든 상태 목록을 삭제한다`() {
        val userId = "userId"

        every { userStatusRepository.removeAllByUserId(userId) } just Runs

        assertDoesNotThrow {
            userStatusService.deleteAllUserStatuses(userId)
        }
    }

    @Test
    fun `유저의 상태들을 삭제한다`() {
        val statusesId = listOf("statusId")
        every { userStatusRepository.removes(statusesId) } just Runs

        assertDoesNotThrow {
            userStatusService.deleteUserStatuses(statusesId)
        }
    }

    @Test
    fun `선택한 상태를 가져온다 - 선택 없음이 아님`() {
        val userId = "userId"
        val userStatus = TestDataFactory.createUserStatus(userId)

        every { userStatusRepository.readSelected(userId) } returns userStatus

        val result = assertDoesNotThrow {
            userStatusService.getSelectedStatus(userId)
        }

        assert(result == userStatus)
    }

    @Test
    fun `선택한 상태를 가져온다 - 선택 없음`() {
        val userId = "userId"
        val userStatus = TestDataFactory.createDefaultUserStatus()

        every { userStatusRepository.readSelected(userId) } returns userStatus

        val result = assertDoesNotThrow {
            userStatusService.getSelectedStatus(userId)
        }

        assert(result.statusId == "none")
        assert(result.userId == userId)
        assert(result.message == "none")
        assert(result.emoji == "none")
        assert(result.isSelected)
    }
}
