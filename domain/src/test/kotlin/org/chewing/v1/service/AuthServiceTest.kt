package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.implementation.auth.*
import org.chewing.v1.repository.auth.LoggedInRepository
import org.chewing.v1.service.auth.AuthService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

@DisplayName("AuthService 테스트")
class AuthServiceTest {
    private val loggedInRepository: LoggedInRepository = mockk()
    private val externalAuthClient: ExternalAuthClient = mockk()
    private val authGenerator: AuthGenerator = AuthGenerator()
    private val authReader: AuthReader = AuthReader(loggedInRepository, externalAuthClient)
    private val authAppender: AuthAppender = AuthAppender(loggedInRepository, externalAuthClient)
    private val authValidator: AuthValidator = AuthValidator()
    private val authUpdater: AuthUpdater = AuthUpdater(loggedInRepository)
    private val authRemover: AuthRemover = AuthRemover(loggedInRepository)
    private val authSender: AuthSender = AuthSender(externalAuthClient)

    private val authService: AuthService = AuthService(
        authReader,
        authAppender,
        authSender,
        authValidator,
        authUpdater,
        authGenerator,
        authRemover,
    )

    @Test
    fun `전화번호 인증 생성`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()

        val verificationCodeSlot = slot<String>()
        val smsMessageSlot = slot<String>()

        every { externalAuthClient.cacheVerificationCode(phoneNumber, capture(verificationCodeSlot)) } just Runs
        every { externalAuthClient.sendSms(phoneNumber, capture(smsMessageSlot)) } just Runs

        authService.createCredential(phoneNumber)

        verify { externalAuthClient.cacheVerificationCode(phoneNumber, any()) }
        verify { externalAuthClient.sendSms(phoneNumber, any()) }
        assert(6 == verificationCodeSlot.captured.length, { "Verification code should be 6 digits" })
    }

    @Test
    fun `전화번호 인증 검증`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "123456"

        every { externalAuthClient.readVerificationCode(phoneNumber) } returns verificationCode

        assertDoesNotThrow {
            authService.verify(phoneNumber, verificationCode)
        }
    }

    @Test
    fun `전화번호 인증번호가 틀려야 한다`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"
        val wrongVerificationCode = "5678"

        every { externalAuthClient.readVerificationCode(phoneNumber) } returns wrongVerificationCode

        val exception = assertThrows<AuthorizationException> {
            authService.verify(phoneNumber, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.WRONG_VERIFICATION_CODE)
    }

    @Test
    fun `전화번호 인증시 인증이 만료된 경우`() {
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "1234"

        every { externalAuthClient.readVerificationCode(phoneNumber) } returns null

        val exception = assertThrows<AuthorizationException> {
            authService.verify(phoneNumber, verificationCode)
        }
        assert(exception.errorCode == ErrorCode.EXPIRED_VERIFICATION_CODE)
    }

    @Test
    fun `로그인 정보 생성`() {
        val userId = TestDataFactory.createUserId()
        val user = TestDataFactory.createAccessUser(userId)
        val refreshToken = TestDataFactory.createRefreshToken()

        every { loggedInRepository.append(any(), any()) } just Runs

        assertDoesNotThrow {
            authService.createLoginInfo(user.userId, refreshToken)
        }
    }

    @Test
    fun `로그 아웃시 토큰이 삭제 되어야함 - 성공`() {
        val refreshToken = TestDataFactory.createRefreshToken()

        every { loggedInRepository.remove(any()) } just Runs

        assertDoesNotThrow {
            authService.logout(refreshToken.token)
        }
    }

    @Test
    fun `jwt 토큰 refresh에 성공해야 한다`() {
        val userId = TestDataFactory.createUserId()
        val refreshToken = TestDataFactory.createRefreshToken()
        val oldRefreshToken = TestDataFactory.createOldRefreshToken()

        every { loggedInRepository.read(oldRefreshToken.token, userId) } returns refreshToken
        every { loggedInRepository.update(any(), any()) } just Runs

        assertDoesNotThrow {
            authService.updateLoginInfo(oldRefreshToken.token, refreshToken, userId)
        }
    }

    @Test
    fun `저장된 jwt 토큰이 없어서 에러가 발생해야 함`() {
        val userId = TestDataFactory.createUserId()
        val refreshToken = TestDataFactory.createRefreshToken()
        val oldRefreshToken = TestDataFactory.createOldRefreshToken()

        every { loggedInRepository.read(oldRefreshToken.token, userId) } returns null

        val exception = assertThrows<AuthorizationException> {
            authService.updateLoginInfo(oldRefreshToken.token, refreshToken, userId)
        }

        assert(exception.errorCode == ErrorCode.INVALID_TOKEN)
    }

    @Test
    fun `비밀번호 암호화`() {
        val password = "1234"
        val encryptedPassword = authService.encryptPassword(password)
        assert(password != encryptedPassword)
    }

    @Test
    fun `비밀번호 검증 - 성공`() {
        val password = "1234"
        val userId = TestDataFactory.createUserId()
        val encryptPassword = authGenerator.hashPassword(password)
        val user = TestDataFactory.createEncryptedUser(userId, encryptPassword)
        assertDoesNotThrow {
            authService.validatePassword(user, password)
        }
    }

    @Test
    fun `비밀번호 검증 - 실패`() {
        val password = "1234"
        val wrongPassword = "5678"
        val userId = TestDataFactory.createUserId()
        val encryptPassword = authGenerator.hashPassword(password)
        val user = TestDataFactory.createEncryptedUser(userId, encryptPassword)

        val exception = assertThrows<AuthorizationException> {
            authService.validatePassword(user, wrongPassword)
        }

        assert(exception.errorCode == ErrorCode.WRONG_PASSWORD)
    }
}
