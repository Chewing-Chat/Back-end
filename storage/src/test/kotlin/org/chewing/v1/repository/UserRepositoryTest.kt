package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.jpa.user.UserRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.MediaProvider
import org.chewing.v1.repository.support.PhoneNumberProvider
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class UserRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Test
    fun `유저 아이디로 읽기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.read(user.userId)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저 아이디로 읽기 - 유저가 존재하지 않음`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.read("notExistUserId")

        assert(result == null)
    }

    @Test
    fun `유저 휴대폰으로 읽기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.readByCredential(phoneNumber, AccessStatus.ACCESS)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저 휴대폰으로 읽기 - 실패 존재하지 않는 계정(PASSWORD 생성 여부, 삭제 여부)`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.DELETE)

        val result = userRepositoryImpl.readByCredential(phoneNumber, AccessStatus.ACCESS)

        assert(result == null)
    }

    @Test
    fun `휴대폰으로 유저 신규 생성 - 상태가 NEED PASSWORD여야 함`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = userRepositoryImpl.append(phoneNumber, userName)

        val result = userRepositoryImpl.read(user.userId)

        assert(result!!.status == AccessStatus.NEED_CREATE_PASSWORD)
    }

    @Test
    fun `유저 삭제`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        userRepositoryImpl.remove(user.userId)

        assert(userJpaRepository.findById(user.userId).get().toUser().status == AccessStatus.DELETE)
    }

    @Test
    fun `유저 삭제 실패 - 유저가 존재하지 않음`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.remove("notExistUserId")

        assert(result == null)
    }

    @Test
    fun `유저 이미지 업데이트 - 기본 사진에서 새로운 사진으로 바뀜`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val media = MediaProvider.buildProfileContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.image.type == media.type)
    }

    @Test
    fun `유저 이미지 업데이트 실패 - 유저가 존재하지 않음`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        val userId = "notExistUserId"

        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val media = MediaProvider.buildProfileContent()

        val result = userRepositoryImpl.updateMedia(userId, media)
        assert(result == null)
    }

    @Test
    fun `유저 게정 읽기 읽기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.read(user.userId)

        assert(result != null)
        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저Ids의 모든 정보 가져오기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)
        val user2 = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val result = userRepositoryImpl.reads(listOf(user.userId, user2.userId))

        assert(result.size == 2)
    }

    @Test
    fun `비밀번호 업데이트 성공`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val newPassword = "newPassword"
        userRepositoryImpl.updatePassword(user.userId, newPassword)

        val result = userJpaRepository.findById(user.userId)

        assert(result.get().toUser().password == newPassword)
    }

    @Test
    fun `비밀번호 업데이트 실패 - 유저가 존재하지 않음`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val newPassword = "newPassword"
        val result = userRepositoryImpl.updatePassword("notExistUserId", newPassword)

        assert(result == null)
    }

    @Test
    fun `상태 메시지 업데이트 성공`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val newStatusMessage = "newStatusMessage"
        userRepositoryImpl.updateStatusMessage(user.userId, newStatusMessage)

        val result = userJpaRepository.findById(user.userId)

        assert(result.get().toUser().statusMessage == newStatusMessage)
    }

    @Test
    fun `상태 메시지 업데이트 실패 - 유저가 존재하지 않음`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        jpaDataGenerator.userEntityData(phoneNumber, userName, AccessStatus.ACCESS)

        val newStatusMessage = "newStatusMessage"
        val result = userRepositoryImpl.updateStatusMessage("notExistUserId", newStatusMessage)

        assert(result == null)
    }
}
