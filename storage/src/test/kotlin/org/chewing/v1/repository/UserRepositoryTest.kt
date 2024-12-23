package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.repository.jpa.user.UserRepositoryImpl
import org.chewing.v1.repository.support.EmailProvider
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.MediaProvider
import org.chewing.v1.repository.support.PhoneProvider
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class UserRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var userJpaRepository: UserJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var userRepositoryImpl: UserRepositoryImpl

    @Test
    fun `유저 아이디로 읽기`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)

        val result = userRepositoryImpl.read(user.userId)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저 이메일로 읽기`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)

        val result = userRepositoryImpl.readByContact(email)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `유저 휴대폰으로 읽기`() {
        val phone = PhoneProvider.buildNormal()
        val user = jpaDataGenerator.userEntityPhoneData(phone)

        val result = userRepositoryImpl.readByContact(phone)

        assert(result!!.userId == user.userId)
    }

    @Test
    fun `이메일로 유저 신규 생성`() {
        val email = EmailProvider.buildNormal()

        val user = userRepositoryImpl.append(email)

        assert(user.userId.isNotEmpty())
    }

    @Test
    fun `휴대폰 으로 유저 신규 생성`() {
        val phone = PhoneProvider.buildNormal()

        val user = userRepositoryImpl.append(phone)

        assert(user.userId.isNotEmpty())
    }

    @Test
    fun `이미 이메일 유저가 있다면 신규 생성하면 안됨`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)

        val result = userRepositoryImpl.append(email)

        assert(result.userId == user.userId)
    }

    @Test
    fun `이미 휴대폰 유저가 있다면 신규 생성하면 안됨`() {
        val phone = PhoneProvider.buildNormal()
        val user = jpaDataGenerator.userEntityPhoneData(phone)

        val result = userRepositoryImpl.append(phone)

        assert(result.userId == user.userId)
    }

    @Test
    fun `유저 삭제`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)

        userRepositoryImpl.remove(user.userId)

        assert(userJpaRepository.findById(user.userId).get().toUser().status == AccessStatus.DELETE)
    }

    @Test
    fun `유저 이미지 업데이트 - 기본 사진에서 새로운 사진으로 바뀜`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val media = MediaProvider.buildProfileContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.image.type == media.type)
    }

    @Test
    fun `유저 이미지 업데이트 - 기본 배경 사진에서 새로운 사진으로 바뀜`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val media = MediaProvider.buildBackgroundContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.backgroundImage.type == media.type)
    }

    @Test
    fun `유저 TTS업데이트`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val media = MediaProvider.buildTTSContent()

        userRepositoryImpl.updateMedia(user.userId, media)
        val result = userJpaRepository.findById(user.userId).get().toTTS()
        assert(result.url == media.url)
    }

    @Test
    fun `유저 이름 변경`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val newName = UserProvider.buildNewUserName()

        userRepositoryImpl.updateName(user.userId, newName)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.name.firstName == newName.firstName)
        assert(user.name.lastName != newName.lastName)
    }

    @Test
    fun `유저 연락처 변경`() {
        val oldEmail = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(oldEmail)
        val newEmail = EmailProvider.buildNew()

        userRepositoryImpl.updateContact(user.userId, newEmail)
        val result = userJpaRepository.findById(user.userId).get().toUserAccount()

        assert(result.emailId != oldEmail.emailId)
    }

    @Test
    fun `유저 접근 변경`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val newContent = UserProvider.buildNewUserContent()

        userRepositoryImpl.updateAccess(user.userId, newContent)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.status == AccessStatus.ACCESS)
        assert(result.name.firstName != user.name.firstName)
        assert(result.birth != user.birth)
        assert(result.name.lastName != user.name.lastName)
    }

    @Test
    fun `연락처가 다른 유저 소유 있는지 확인`() {
        val phone = PhoneProvider.buildNormal()
        jpaDataGenerator.userEntityPhoneData(phone)
        val newUserId = generateUserId()

        val result = userRepositoryImpl.checkContactIsUsedByElse(phone, newUserId)

        assert(result)
    }

    @Test
    fun `이메일이 다른 유저 소유 인지 확인`() {
        val email = EmailProvider.buildNormal()
        jpaDataGenerator.userEntityEmailData(email)

        val newUserId = generateUserId()

        val result = userRepositoryImpl.checkContactIsUsedByElse(email, newUserId)

        assert(result)
    }

    @Test
    fun `유저 생일 변경`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)
        val newBirth = UserProvider.buildNewBirth()

        userRepositoryImpl.updateBirth(user.userId, newBirth)
        val result = userJpaRepository.findById(user.userId).get().toUser()

        assert(result.birth == newBirth)
        assert(result.birth != user.birth)
    }

    @Test
    fun `유저 게정 읽기 읽기`() {
        val email = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email)

        val result = userRepositoryImpl.readAccount(user.userId)

        assert(result != null)
        assert(result!!.user.userId == user.userId)
        assert(result.emailId == email.emailId)
    }

    @Test
    fun `유저Ids의 모든 정보 가져오기`() {
        val email1 = EmailProvider.buildNormal()
        val user = jpaDataGenerator.userEntityEmailData(email1)

        val email2 = EmailProvider.buildNormal()
        val user2 = jpaDataGenerator.userEntityEmailData(email2)

        val result = userRepositoryImpl.reads(listOf(user.userId, user2.userId))

        assert(result.size == 2)
    }

    private fun generateUserId(): String = UUID.randomUUID().toString()
}
