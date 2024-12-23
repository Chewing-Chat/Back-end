package org.chewing.v1.facade

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.model.contact.ContactType
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
        val email = TestDataFactory.createEmail("123")
        val user = TestDataFactory.createUser(userId)
        val emailAddress = TestDataFactory.createEmailAddress()
        val loginInfo = TestDataFactory.createLoginInfo(user)
        val device = TestDataFactory.createDevice()

        every { authService.verify(any(), any()) } returns email
        every { userService.createUser(any(), any(), any()) } returns user
        every { authService.createLoginInfo(any()) } returns loginInfo

        val result = assertDoesNotThrow {
            accountFacade.loginAndCreateUser(emailAddress, "123", "testAppToken", device)
        }
        assert(result == loginInfo)
    }

    @Test
    fun `인증 정보 변경`() {
        val userId = "123"
        val email = TestDataFactory.createEmail("123")
        val emailAddress = TestDataFactory.createEmailAddress()

        every { authService.verify(any(), any()) } returns email
        every { userService.updateUserContact(any(), any()) } just Runs

        accountFacade.changeCredential(userId, emailAddress, "123")
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
        val emailId = "123"
        val userAccount = TestDataFactory.createUserAccount(emailId, null)
        val email = TestDataFactory.createEmail("123")

        every { userService.getUserAccount(any()) } returns userAccount
        every { authService.getContactById(userAccount.emailId!!, ContactType.EMAIL) } returns email

        val result = assertDoesNotThrow {
            accountFacade.getAccount(userId)
        }
        assert(result.user == userAccount.user)
        assert(result.emailAddress == email.emailAddress)
        assert(result.phoneNumber == "none")
        assert(result.countryCode == "none")
    }

    @Test
    fun `계정 조회 - 이메일은 none이 들어가야 함`() {
        val userId = "123"
        val phoneId = "123"
        val userAccount = TestDataFactory.createUserAccount(null, phoneId)
        val phone = TestDataFactory.createPhone("123")

        every { userService.getUserAccount(any()) } returns userAccount
        every { authService.getContactById(userAccount.phoneId!!, ContactType.PHONE) } returns phone

        val result = assertDoesNotThrow {
            accountFacade.getAccount(userId)
        }
        assert(result.user == userAccount.user)
        assert(result.emailAddress == "none")
        assert(result.phoneNumber == phone.number)
        assert(result.countryCode == phone.countryCode)
    }
}
