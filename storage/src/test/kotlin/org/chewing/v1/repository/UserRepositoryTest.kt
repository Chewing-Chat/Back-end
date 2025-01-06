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
        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val result = userRepositoryImpl.read(user.userId)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저 휴대폰으로 읽기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()
        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val result = userRepositoryImpl.readByCredential(phoneNumber)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `휴대폰 으로 유저 신규 생성`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = userRepositoryImpl.append(phoneNumber, userName)

        assert(user.userId.isNotEmpty())
    }

    @Test
    fun `이미 휴대폰 유저가 있다면 신규 생성하면 안됨`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val result = userRepositoryImpl.append(phoneNumber, userName)

        assert(result.userId == user.userId)
    }

    @Test
    fun `유저 삭제`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        userRepositoryImpl.remove(user.userId)

        assert(userJpaRepository.findById(user.userId).get().toUser().status == AccessStatus.DELETE)
    }

    @Test
    fun `유저 이미지 업데이트 - 기본 사진에서 새로운 사진으로 바뀜`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val media = MediaProvider.buildProfileContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.image.type == media.type)
    }

    @Test
    fun `유저 이미지 업데이트 - 기본 배경 사진에서 새로운 사진으로 바뀜`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val media = MediaProvider.buildBackgroundContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.backgroundImage.type == media.type)
    }

    @Test
    fun `유저 게정 읽기 읽기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val result = userRepositoryImpl.read(user.userId)

        assert(result != null)
        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저Ids의 모든 정보 가져오기`() {
        val phoneNumber = PhoneNumberProvider.buildPhoneNumber()
        val userName = UserProvider.buildUserName()

        val user = jpaDataGenerator.userEntityData(phoneNumber, userName)
        val user2 = jpaDataGenerator.userEntityData(phoneNumber, userName)

        val result = userRepositoryImpl.reads(listOf(user.userId, user2.userId))

        assert(result.size == 2)
    }
}
