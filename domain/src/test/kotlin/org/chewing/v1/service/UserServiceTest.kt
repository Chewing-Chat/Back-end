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
    private val userValidator = UserValidator(userRepository)

    private val userService =
        UserService(userReader, fileHandler, userUpdater, userValidator, userRemover, userAppender)

    @Test
    fun `유저 계정 정보를 가져와야함`() {
        val userId = "userId"
        val user = TestDataFactory.createAccessUser(userId)

        every { userRepository.read(userId) } returns user
        val result = assertDoesNotThrow {
            userService.getUser(userId)
        }

        assert(result == user)
    }

    @Test
    fun `유저 계정 정보가 없어야함`() {
        val userId = "userId"

        every { userRepository.read(userId) } returns null

        val result = assertThrows<NotFoundException> {
            userService.getUser(userId)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저의 정보를 가져오는 활성화된 유저임`() {
        val userId = "userId"
        val user = TestDataFactory.createAccessUser(userId)

        every { userRepository.read(userId) } returns user

        assertDoesNotThrow {
            userService.getAccessUser(userId)
        }
    }

    @Test
    fun `유저의 정보를 가져오는 활성화된 유저가 아님`() {
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
        val user = TestDataFactory.createAccessUser(userId)

        every { userRepository.remove(userId) } returns user
        every { fileHandler.handleOldFile(any()) } just Runs

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

        val users = listOf(TestDataFactory.createAccessUser(userId))

        every { userRepository.reads(listOf(userId)) } returns users

        val result = assertDoesNotThrow {
            userService.getUsers(listOf(userId))
        }

        assert(result == users)
    }

    @Test
    fun `유저의 비밀번호를 업데이트 한다 - 성공`() {
        val userId = "userId"
        val password = "password"

        every { userRepository.updatePassword(userId, password) } returns userId

        assertDoesNotThrow {
            userService.updatePassword(userId, password)
        }
    }

    @Test
    fun `유저의 비밀번호를 업데이트 한다 - 실패(유저가 존재하지 않음)`() {
        val userId = "userId"
        val password = "password"

        every { userRepository.updatePassword(userId, password) } returns null

        val result = assertThrows<NotFoundException> {
            userService.updatePassword(userId, password)
        }

        assert(result.errorCode == ErrorCode.USER_NOT_FOUND)
    }

    @Test
    fun `유저 생성 성공한다 - 유저가 존재하지 않음`() {
        val userId = "userId"
        val credential = TestDataFactory.createPhoneNumber()
        val appToken = "appToken"
        val device = TestDataFactory.createDevice()
        val userName = "userName"
        val user = TestDataFactory.createNotAccessUser(userId)

        every { userRepository.append(credential, userName) } returns user
        every { userRepository.readByCredential(credential) } returns null
        every { pushNotificationRepository.append(device, appToken, user) } just Runs
        every { pushNotificationRepository.remove(device) } just Runs

        assertDoesNotThrow {
            userService.createUser(credential, appToken, device, userName)
        }
    }

    @Test
    fun `유저 생성 실패한다 - 유저가 이미 존재함`() {
        val userId = "userId"
        val credential = TestDataFactory.createPhoneNumber()
        val appToken = "appToken"
        val device = TestDataFactory.createDevice()
        val userName = "userName"
        val user = TestDataFactory.createAccessUser(userId)

        every { userRepository.append(credential, userName) } returns user
        every { userRepository.readByCredential(credential) } returns user
        every { pushNotificationRepository.append(device, appToken, user) } just Runs
        every { pushNotificationRepository.remove(device) } just Runs

        val result = assertThrows<ConflictException> {
            userService.createUser(credential, appToken, device, userName)
        }

        assert(result.errorCode == ErrorCode.USER_ALREADY_CREATED)
    }
}
