package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.implementation.auth.*
import org.chewing.v1.model.contact.ContactType
import org.chewing.v1.repository.auth.EmailRepository
import org.chewing.v1.repository.auth.LoggedInRepository
import org.chewing.v1.repository.auth.PhoneRepository
import org.chewing.v1.repository.user.UserRepository
import org.chewing.v1.service.auth.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@DisplayName("AuthService 테스트")
class AuthServiceTest {
    private val phoneRepository: PhoneRepository = mockk()
    private val emailRepository: EmailRepository = mockk()
    private val loggedInRepository: LoggedInRepository = mockk()
    private val userRepository: UserRepository = mockk()
    private val externalAuthClient: ExternalAuthClient = mockk()
    private val authReader: AuthReader = AuthReader(phoneRepository, emailRepository, loggedInRepository)
    private val authAppender: AuthAppender = AuthAppender(loggedInRepository, emailRepository, phoneRepository)
    private val authValidator: AuthValidator = AuthValidator(userRepository, phoneRepository, emailRepository)
    private val authUpdater: AuthUpdater = AuthUpdater(loggedInRepository)
    private val jwtTokenProvider: JwtTokenProvider = JwtTokenProvider(
        "mysecretkey12345asdfvasdfvhjaaaaaaaaaaaaaaaaaaaaaaaaaslfdjasdlkr231243123412",
        1000L * 60 * 60 * 24 * 7,
        1000L * 60 * 60 * 24 * 30,
    )
    private val authRemover: AuthRemover = AuthRemover(loggedInRepository)
    private val authSender: AuthSender = AuthSender(externalAuthClient)

    private val authService: AuthService = AuthService(
        authReader,
        authAppender,
        authSender,
        authValidator,
        authUpdater,
        jwtTokenProvider,
        authRemover,
    )

    @Test
    fun `이메일 인증 생성`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val verificationCode = "1234"

        every { emailRepository.appendIfNotExists(emailAddress) } returns verificationCode
        every { externalAuthClient.sendEmail(emailAddress, verificationCode) } just Runs

        authService.createCredential(emailAddress)

        verify { emailRepository.appendIfNotExists(emailAddress) }
        verify { externalAuthClient.sendEmail(emailAddress, verificationCode) }
    }

    @Test
    fun `전화번호 인증 생성`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"

        every { phoneRepository.appendIfNotExists(phoneNumber) } returns verificationCode
        every { externalAuthClient.sendSms(phoneNumber, verificationCode) } just Runs

        authService.createCredential(phoneNumber)

        verify { phoneRepository.appendIfNotExists(phoneNumber) }
        verify { externalAuthClient.sendSms(phoneNumber, verificationCode) }
    }

    @Test
    fun `이메일 인증 검증`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val verificationCode = "1234"
        val email = TestDataFactory.createEmail(verificationCode)

        every { emailRepository.read(emailAddress) } returns email

        val result = authService.verify(emailAddress, verificationCode)

        assert(result == email)
    }

    @Test
    fun `이메일 인증번호가 틀려야 한다`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val verificationCode = "1234"
        val email = TestDataFactory.createEmail("4321")

        every { emailRepository.read(emailAddress) } returns email

        val exception = assertThrows<ConflictException> {
            authService.verify(emailAddress, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.WRONG_VALIDATE_CODE)
    }

    @Test
    fun `이메일 인증번호가 만료되어야 한다`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val verificationCode = "1234"
        val email = TestDataFactory.createExpiredEmail(verificationCode)

        every { emailRepository.read(emailAddress) } returns email

        val exception = assertThrows<ConflictException> {
            authService.verify(emailAddress, verificationCode)
        }

        assert(exception.errorCode == ErrorCode.EXPIRED_VALIDATE_CODE)
    }

    @Test
    fun `전화번호 인증 검증`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"
        val phone = TestDataFactory.createPhone(verificationCode)

        every { phoneRepository.read(phoneNumber) } returns phone

        val result = authService.verify(phoneNumber, verificationCode)

        assert(result == phone)
    }

    @Test
    fun `전화번호 인증번호가 틀려야 한다`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"
        val phone = TestDataFactory.createPhone("4321")

        every { phoneRepository.read(phoneNumber) } returns phone

        val exception = assertThrows<ConflictException> {
            authService.verify(phoneNumber, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.WRONG_VALIDATE_CODE)
    }

    @Test
    fun `이메일 인증시 인증요청을 하지 않고 잘몰된 접근 한 경우`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val verificationCode = "1234"

        every { emailRepository.read(emailAddress) } returns null

        val exception = assertThrows<ConflictException> {
            authService.verify(emailAddress, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.WRONG_ACCESS)
    }

    @Test
    fun `전화번호 인증시 인증요청을 하지 않고 잘몰된 접근 한 경우`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"

        every { phoneRepository.read(phoneNumber) } returns null

        val exception = assertThrows<ConflictException> {
            authService.verify(phoneNumber, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.WRONG_ACCESS)
    }

    @Test
    fun `로그인 정보 생성`() {
        val userId = "1234"
        val user = TestDataFactory.createUser(userId)

        every { loggedInRepository.append(any(), any()) } just Runs

        val result = authService.createLoginInfo(user)

        assert(result.loginType == user.status)
    }

    @Test
    fun `로그 아웃시 토큰이 삭제 되어야함 - 성공`() {
        val userId = "1234"
        val refreshToken = jwtTokenProvider.createRefreshToken(userId)

        every { loggedInRepository.remove(any()) } just Runs

        assertDoesNotThrow {
            authService.logout(refreshToken.token)
        }
    }

    @Test
    fun `jwt 토큰 refresh에 성공해야 한다`() {
        val userId = "1234"
        val refreshToken = jwtTokenProvider.createRefreshToken(userId)

        every { loggedInRepository.read(refreshToken.token, userId) } returns refreshToken
        every { loggedInRepository.update(any(), any()) } just Runs

        assertDoesNotThrow {
            authService.refreshJwtToken(refreshToken.token)
        }
    }

    @Test
    fun `저장된 jwt 토큰이 없어서 에러가 발생해야 함`() {
        val userId = "1234"
        val refreshToken = jwtTokenProvider.createRefreshToken("1234")

        every { loggedInRepository.read(refreshToken.token, userId) } returns null

        val exception = assertThrows<AuthorizationException> {
            authService.refreshJwtToken(refreshToken.token)
        }

        assert(exception.errorCode == ErrorCode.INVALID_TOKEN)
    }

    @Test
    fun `새로운 이메일 변경 시 다른 사람이 사용하고 있음`() {
        val userId = "1234"
        val verificationCode = "1234"
        val emailAddress = TestDataFactory.createEmailAddress()
        val email = TestDataFactory.createEmail(verificationCode)

        every { emailRepository.read(emailAddress) } returns email
        every { userRepository.checkContactIsUsedByElse(email, userId) } returns true

        val exception = assertThrows<ConflictException> {
            authService.createCredentialNotUsed(userId, emailAddress)
        }

        assert(exception.errorCode == ErrorCode.EMAIL_ADDRESS_IS_USED)
    }

    @Test
    fun `새로운 전화번호 변경 시 다른 사람이 사용하고 있음`() {
        val userId = "1234"
        val verificationCode = "1234"
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val phone = TestDataFactory.createPhone(verificationCode)

        every { phoneRepository.read(phoneNumber) } returns phone
        every { userRepository.checkContactIsUsedByElse(phone, userId) } returns true

        val exception = assertThrows<ConflictException> {
            authService.createCredentialNotUsed(userId, phoneNumber)
        }

        assert(exception.errorCode == ErrorCode.PHONE_NUMBER_IS_USED)
    }

    @Test
    fun `전화번호 변경을 위한 생성시 기존의 사용자가 나인 경우 성공`() {
        val userId = "1234"
        val verificationCode = "1234"
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val phone = TestDataFactory.createPhone(verificationCode)

        every { phoneRepository.read(phoneNumber) } returns phone
        every { userRepository.checkContactIsUsedByElse(phone, userId) } returns false
        every { phoneRepository.appendIfNotExists(phoneNumber) } returns verificationCode
        every { externalAuthClient.sendSms(phoneNumber, verificationCode) } just Runs

        assertDoesNotThrow {
            authService.createCredentialNotUsed(userId, phoneNumber)
        }
    }

    @Test
    fun `이메일 변경을 위한 생성시 기존의 사용자가 나인 경우 성공`() {
        val userId = "1234"
        val verificationCode = "1234"
        val emailAddress = TestDataFactory.createEmailAddress()
        val email = TestDataFactory.createEmail(verificationCode)

        every { emailRepository.read(emailAddress) } returns email
        every { userRepository.checkContactIsUsedByElse(email, userId) } returns false
        every { emailRepository.appendIfNotExists(emailAddress) } returns verificationCode
        every { externalAuthClient.sendEmail(emailAddress, verificationCode) } just Runs

        assertDoesNotThrow {
            authService.createCredentialNotUsed(userId, emailAddress)
        }
    }

    @Test
    fun `전화번호 변경을 위한 생성시 기존의 사용자가 존재하지 않음`() {
        val userId = "1234"
        val verificationCode = "1234"
        val phoneNumber = TestDataFactory.createPhoneNumber()

        every { phoneRepository.read(phoneNumber) } returns null
        every { phoneRepository.appendIfNotExists(phoneNumber) } returns verificationCode
        every { externalAuthClient.sendSms(phoneNumber, verificationCode) } just Runs

        assertDoesNotThrow {
            authService.createCredentialNotUsed(userId, phoneNumber)
        }
    }

    @Test
    fun `이메일 변경을 위한 생성시 기존의 사용자가 존재 하지 않음`() {
        val userId = "1234"
        val verificationCode = "1234"
        val emailAddress = TestDataFactory.createEmailAddress()

        every { emailRepository.read(emailAddress) } returns null
        every { emailRepository.appendIfNotExists(emailAddress) } returns verificationCode
        every { externalAuthClient.sendEmail(emailAddress, verificationCode) } just Runs

        assertDoesNotThrow {
            authService.createCredentialNotUsed(userId, emailAddress)
        }
    }

    @Test
    fun `이메일 정보를 id를 통해 가져와야함`() {
        val contactId = "1234"
        val contactType = ContactType.EMAIL
        val testVerifyCode = "1234"
        val email = TestDataFactory.createEmail(testVerifyCode)

        every { emailRepository.readById(contactId) } returns email

        val result = authService.getContactById(contactId, contactType)

        assert(result == email)
    }

    @Test
    fun `이메일 정보를 id를 통해 가져와야함 - 정보가 존재하지 않음`() {
        val contactId = "1234"
        val contactType = ContactType.EMAIL

        every { emailRepository.readById(contactId) } returns null

        val result = authService.getContactById(contactId, contactType)

        assert(result == null)
    }

    @Test
    fun `전화번호 정보를 id를 통해 가져와야함`() {
        val contactId = "1234"
        val contactType = ContactType.PHONE
        val testVerifyCode = "1234"
        val phone = TestDataFactory.createPhone(testVerifyCode)

        every { phoneRepository.readById(contactId) } returns phone

        val result = authService.getContactById(contactId, contactType)

        assert(result == phone)
    }

    @Test
    fun `전화번호 정보를 id를 통해 가져와야함 - 정보가 존재하지 않음`() {
        val contactId = "1234"
        val contactType = ContactType.PHONE

        every { phoneRepository.readById(contactId) } returns null

        val result = authService.getContactById(contactId, contactType)

        assert(result == null)
    }

    @Test
    fun `이메일 정보를 이메일로 가져와야함`() {
        val emailAddress = TestDataFactory.createEmailAddress()
        val email = TestDataFactory.createEmail("1234")

        every { emailRepository.read(emailAddress) } returns email

        val result = authService.getContact(emailAddress)

        assert(result == email)
    }

    @Test
    fun `전화번호 정보를 전화번호로 가져와야함`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val phone = TestDataFactory.createPhone("1234")

        every { phoneRepository.read(phoneNumber) } returns phone

        val result = authService.getContact(phoneNumber)

        assert(result == phone)
    }
}
