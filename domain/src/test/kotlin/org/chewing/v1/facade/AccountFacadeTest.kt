package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.auth.AuthService
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AccountFacadeTest {
    private val authService: AuthService = mockk()
    private val userService: UserService = mockk()
    private val scheduleService: ScheduleService = mockk()

    private val accountFacade = AccountFacade(authService, userService, scheduleService)

    @Test
    fun `유저 생성`() {
        val userId = TestDataFactory.createUserId()
        val userInfo = TestDataFactory.createUserInfo(userId, AccessStatus.ACCESS)
        val device = TestDataFactory.createDevice()
        val localPhoneNumber = TestDataFactory.createLocalPhoneNumber()

        every { authService.verify(any(), any()) } just Runs
        every { userService.createUser(any(), any(), any(), any()) } returns userInfo

        val result = assertDoesNotThrow {
            accountFacade.createUser(localPhoneNumber, "123", "testAppToken", device, "testUserName")
        }

        assert(result == userId)
    }

    @Test
    fun `계정 삭제`() {
        val userId = TestDataFactory.createUserId()

        every { userService.deleteUser(any()) } just Runs
        every { scheduleService.deleteAllParticipant(any()) } just Runs

        accountFacade.deleteAccount(userId)

        verify { userService.deleteUser(userId) }
    }
}
