package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
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
        val userId = "123"
        val user = TestDataFactory.createAccessUser(userId)
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val device = TestDataFactory.createDevice()
        val jwtToken = TestDataFactory.createJwtToken()

        every { authService.verify(any(), any()) } just Runs
        every { userService.createUser(any(), any(), any(), any()) } returns user
        every { authService.createToken(user) } returns jwtToken

        val result = assertDoesNotThrow {
            accountFacade.createUser(phoneNumber, "123", "testAppToken", device, "testUserName")
        }
        assert(result == jwtToken)
    }

    @Test
    fun `계정 삭제`() {
        val userId = "123"

        every { userService.deleteUser(any()) } just Runs
        every { scheduleService.deleteParticipant(any()) } just Runs

        accountFacade.deleteAccount(userId)

        verify { userService.deleteUser(userId) }
    }
}
