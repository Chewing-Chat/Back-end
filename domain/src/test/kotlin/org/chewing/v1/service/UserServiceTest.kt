package org.chewing.v1.service

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.TestDataFactory
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.implementation.media.FileHandler
import org.chewing.v1.implementation.user.user.*
import org.chewing.v1.model.media.FileCategory
import org.chewing.v1.repository.user.PushNotificationRepository
import org.chewing.v1.repository.user.UserRepository
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val pushNotificationRepository: PushNotificationRepository = mockk()
    private val fileHandler: FileHandler = mockk()

    private val userReader =
        UserReader(userRepository, pushNotificationRepository)
    private val userUpdater = UserUpdater(userRepository)
    private val userRemover = UserRemover(userRepository, pushNotificationRepository)
    private val userAppender =
        UserAppender(userRepository, pushNotificationRepository)
    private val userValidator = UserValidator()

    private val userService =
        UserService(userReader, fileHandler, userUpdater, userValidator, userRemover, userAppender)

    @Test
    fun `유저 계정 정보를 가져와야함`() {
        val userId = "userId"
        val emailId = "emailId"
        val phoneId = "phoneId"
        val userAccount = TestDataFactory.createUserAccount(emailId, phoneId)

        every { userRepository.readAccount(userId) } returns userAccount

        val result = assertDoesNotThrow {
            userService.getUserAccount(userId)
        }

        assert(result == userAccount)
    }

    @Test
    fun `유저 계정 정보가 없어야함`() {
        val userId = "userId"

        every { userRepository.readAccount(userId) } returns null

        val result = assertThrows<NotFoundException> {
            userService.getUserAccount(userId)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저를 이메일로 생성해야함`() {
        val verificationCode = "1234"
        val userId = "userId"
        val contact = TestDataFactory.createEmail(verificationCode)
        val appToken = TestDataFactory.createAppToken()
        val device = TestDataFactory.createDevice()
        val user = TestDataFactory.createUser(userId)

        every { userRepository.append(contact) } returns user
        every { pushNotificationRepository.remove(device) } just Runs
        every { pushNotificationRepository.append(device, appToken, user) } just Runs

        val result = assertDoesNotThrow {
            userService.createUser(contact, appToken, device)
        }

        assert(user == result)
    }

    @Test
    fun `유저의 접근 정보를 업데이트 해야함`() {
        val userId = "userId"
        val userContent = TestDataFactory.createUserContent()

        every { userRepository.updateAccess(userId, userContent) } returns userId

        assertDoesNotThrow {
            userService.makeAccess(userId, userContent)
        }
    }

    @Test
    fun `유저의 접근 정보 업데이트 실패 - 유저가 없음`() {
        val userId = "userId"
        val userContent = TestDataFactory.createUserContent()

        every { userRepository.updateAccess(userId, userContent) } returns null

        val result = assertThrows<NotFoundException> {
            userService.makeAccess(userId, userContent)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `생일을 업데이트 해야함`() {
        val userId = "userId"
        val birth = "1990-01-01"

        every { userRepository.updateBirth(userId, birth) } returns userId

        assertDoesNotThrow {
            userService.updateBirth(userId, birth)
        }
    }

    @Test
    fun `생일 업데이트 실패 - 유저가 없음`() {
        val userId = "userId"
        val birth = "1990-01-01"

        every { userRepository.updateBirth(userId, birth) } returns null

        val result = assertThrows<NotFoundException> {
            userService.updateBirth(userId, birth)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 이름을 업데이트 해야함`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()

        every { userRepository.updateName(userId, userName) } returns userId

        assertDoesNotThrow {
            userService.updateName(userId, userName)
        }
    }

    @Test
    fun `유저의 이름 업데이트 실패 - 유저가 없음`() {
        val userId = "userId"
        val userName = TestDataFactory.createUserName()

        every { userRepository.updateName(userId, userName) } returns null

        val result = assertThrows<NotFoundException> {
            userService.updateName(userId, userName)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 연락처를 업데이트 해야함`() {
        val userId = "userId"
        val verificationCode = "1234"
        val contact = TestDataFactory.createPhone(verificationCode)

        every { userRepository.updateContact(userId, contact) } returns userId

        assertDoesNotThrow {
            userService.updateUserContact(userId, contact)
        }
    }

    @Test
    fun `유저의 이메일을 업데이트 해야함`() {
        val userId = "userId"
        val verificationCode = "1234"
        val contact = TestDataFactory.createEmail(verificationCode)

        every { userRepository.updateContact(userId, contact) } returns userId

        assertDoesNotThrow {
            userService.updateUserContact(userId, contact)
        }
    }

    @Test
    fun `유저의 연락처 업데이트 실패 - 유저가 없음`() {
        val userId = "userId"
        val verificationCode = "1234"
        val contact = TestDataFactory.createPhone(verificationCode)

        every { userRepository.updateContact(userId, contact) } returns null

        val result = assertThrows<NotFoundException> {
            userService.updateUserContact(userId, contact)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 이메일 업데이트 실패 - 유저가 없음`() {
        val userId = "userId"
        val verificationCode = "1234"
        val contact = TestDataFactory.createEmail(verificationCode)

        every { userRepository.updateContact(userId, contact) } returns null

        val result = assertThrows<NotFoundException> {
            userService.updateUserContact(userId, contact)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 통합된 정보를 가져와야함`() {
        val userId = "userId"
        val user = TestDataFactory.createUser(userId)

        every { userRepository.read(userId) } returns user

        val result = assertDoesNotThrow {
            userService.getAccessUser(userId)
        }

        assert(result == user)
    }

    @Test
    fun `유저의 통합된 정보를 가져오는데 유저가 없음`() {
        val userId = "userId"

        every { userRepository.read(userId) } returns null

        val result = assertThrows<NotFoundException> {
            userService.getAccessUser(userId)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 통합던 정보를 가져오는 활성화된 유저가 아님`() {
        val userId = "userId"
        val user = TestDataFactory.createNotAccessUser()

        every { userRepository.read(userId) } returns user

        val result = assertThrows<ConflictException> {
            userService.getAccessUser(userId)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_ACCESS)
    }

    @Test
    fun `유저의 파일을 카테고리에 따라 없데이트 해야함`() {
        val userId = "userId"
        val fileData = TestDataFactory.createFileData()
        val media = TestDataFactory.createProfileMedia()

        every { userRepository.updateMedia(userId, media) } returns media
        every { fileHandler.handleNewFile(userId, fileData, FileCategory.PROFILE) } returns media
        every { fileHandler.handleOldFile(any()) } just Runs

        assertDoesNotThrow {
            userService.updateFile(fileData, userId, FileCategory.PROFILE)
        }
    }

    @Test
    fun `유저의 파일을 카테고리에 따라 업데이트 할때 유저가 존재하지 않음`() {
        val userId = "userId"
        val fileData = TestDataFactory.createFileData()
        val media = TestDataFactory.createProfileMedia()

        every { userRepository.updateMedia(userId, media) } returns null
        every { fileHandler.handleNewFile(userId, fileData, FileCategory.PROFILE) } returns media

        val result = assertThrows<NotFoundException> {
            userService.updateFile(fileData, userId, FileCategory.PROFILE)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저를 삭제함`() {
        val userId = "userId"
        val user = TestDataFactory.createUser(userId)

        every { userRepository.remove(userId) } returns user
        every { fileHandler.handleOldFiles(any()) } just Runs

        assertDoesNotThrow {
            userService.deleteUser(userId)
        }
    }

    @Test
    fun `유저를 삭제할때 유저가 없음`() {
        val userId = "userId"
        every { userRepository.remove(userId) } returns null

        val result = assertThrows<NotFoundException> {
            userService.deleteUser(userId)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저 아이디들로 해당 유저가 포합된 유저들을 가져온다`() {
        val userId = "userId"

        val users = listOf(TestDataFactory.createUser(userId))

        every { userRepository.reads(listOf(userId)) } returns users

        val result = assertDoesNotThrow {
            userService.getUsers(listOf(userId))
        }

        assert(result == users)
    }

    @Test
    fun `유저의 연락처로 유저를 가져온다`() {
        val contact = TestDataFactory.createPhone("1234")
        val userId = "userId"
        val user = TestDataFactory.createUser(userId)

        every { userRepository.readByContact(contact) } returns user

        val result = assertDoesNotThrow {
            userService.getUserByContact(contact)
        }

        assert(result == user)
    }

    @Test
    fun `유저의 연락처로 유저를 가져올때 유저가 없음`() {
        val contact = TestDataFactory.createPhone("1234")

        every { userRepository.readByContact(contact) } returns null

        val result = assertThrows<NotFoundException> {
            userService.getUserByContact(contact)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }
}
