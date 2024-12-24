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
import org.chewing.v1.service.user.UserStatusService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class AccountFacadeTest {
    private val authService: AuthService = mockk()
    private val userService: UserService = mockk()
    private val userStatusService: UserStatusService = mockk()
    private val scheduleService: ScheduleService = mockk()

    private val accountFacade = AccountFacade(authService, userService, userStatusService, scheduleService)

    @Test
    fun `로그인 및 유저 생성`() {
        val userId = "123"
        val user = TestDataFactory.createUser(userId)
        val phone = TestDataFactory.createPhone("123")
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val loginInfo = TestDataFactory.createLoginInfo(user)
        val device = TestDataFactory.createDevice()

        every { authService.verify(any(), any()) } returns phone
        every { userService.createUser(any(), any(), any()) } returns user
        every { authService.createLoginInfo(any()) } returns loginInfo

        val result = assertDoesNotThrow {
            accountFacade.loginAndCreateUser(phoneNumber, "123", "testAppToken", device)
        }
        assert(result == loginInfo)
    }

    @Test
    fun `인증 정보 변경`() {
        val userId = "123"
        val phone = TestDataFactory.createPhone("123")
        val phoneNumber = TestDataFactory.createPhoneNumber()

        every { authService.verify(any(), any()) } returns phone
        every { userService.updateUserContact(any(), any()) } just Runs

        accountFacade.changeCredential(userId, phoneNumber, "123")
    }

    @Test
    fun `계정 삭제`() {
        val userId = "123"

        every { userService.deleteUser(any()) } just Runs
        every { userStatusService.deleteAllUserStatuses(any()) } just Runs
        every { scheduleService.deleteUsers(any()) } just Runs

        accountFacade.deleteAccount(userId)

        verify { userService.deleteUser(userId) }
    }

    @Test
    fun `계정 조회 - 휴대폰은 none이 들어가야 함`() {
        val userId = "123"
        val userAccount = TestDataFactory.createUserAccount(null)

        every { userService.getUserAccount(any()) } returns userAccount

        val result = assertDoesNotThrow {
            accountFacade.getAccount(userId)
        }
        assert(result.user == userAccount.user)
        assert(result.phoneNumber == "none")
        assert(result.countryCode == "none")
    }
}
