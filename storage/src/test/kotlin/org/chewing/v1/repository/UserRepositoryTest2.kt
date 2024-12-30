package org.chewing.v1.repository

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.jparepository.user.UserJpaRepository
import org.chewing.v1.repository.jpa.user.UserRepositoryImpl
import org.chewing.v1.repository.support.MediaProvider
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import java.util.*

class UserRepositoryTest2 {
    private val userJpaRepository: UserJpaRepository = mockk()

    private var userRepositoryImpl: UserRepositoryImpl = UserRepositoryImpl(userJpaRepository)

    @Test
    fun `유저 아이디로 읽기 - 실패(유저를 찾을 수 없음)`() {
        val userId = generateUserId()
        every { userJpaRepository.findById(userId) } returns Optional.empty()

        val result = userRepositoryImpl.read(userId)

        assert(result == null)
    }

    @Test
    fun `유저 삭제 - 실패(유저를 찾을 수 없음)`() {
        val userId = generateUserId()

        every { userJpaRepository.findById(userId) } returns Optional.empty()

        val result = userRepositoryImpl.remove(userId)
        assert(result == null)
    }

    @Test
    fun `유저 이미지 변환 - 실패(유저를 찾을 수 없음)`() {
        val media = MediaProvider.buildProfileContent()
        val userId = generateUserId()
        val user = UserProvider.buildNormal(userId)

        every { userJpaRepository.findById(user.userId) } returns Optional.empty()

        val result = userRepositoryImpl.updateMedia(user.userId, media)
        assert(result == null)
    }

    @Test
    fun `유저 배경사진 변환 실패(유저를 찾을 수 없음)`() {
        val media = MediaProvider.buildBackgroundContent()
        val userId = generateUserId()
        val user = UserProvider.buildNormal(userId)

        every { userJpaRepository.findById(user.userId) } returns Optional.empty()

        val result = userRepositoryImpl.updateMedia(user.userId, media)
        assert(result == null)
    }

    private fun generateUserId(): String = UUID.randomUUID().toString()
}
