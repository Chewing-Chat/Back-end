package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.repository.jpa.user.PushNotificationRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.PushTokenProvider
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class PushNotificationTest : JpaContextTest() {
    @Autowired
    private lateinit var pushNotificationJpaRepository: PushNotificationJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var pushNotificationRepositoryImpl: PushNotificationRepositoryImpl

    @Test
    fun `푸시 알림을 위한 정보 저장에 성공`() {
        val userId = generateUserId()
        val user = UserProvider.buildNormal(userId)
        val device = PushTokenProvider.buildDeviceNormal()
        val appToken = PushTokenProvider.buildAppTokenNormal()
        pushNotificationRepositoryImpl.append(device, appToken, user)
        assert(pushNotificationJpaRepository.findAllByUserId(userId).isNotEmpty())
    }

    @Test
    fun `푸시 알림을 위한 정보 삭제에 성공`() {
        val userId = generateUserId()
        val pushNotification = jpaDataGenerator.pushNotificationData(userId)
        pushNotificationRepositoryImpl.remove(pushNotification.device)
        assert(pushNotificationJpaRepository.findById(pushNotification.pushTokenId).isEmpty)
    }

    @Test
    fun `푸시 알림을 위한 정보 전체 삭제에 성공`() {
        val userId = generateUserId()
        jpaDataGenerator.pushNotificationData(userId)
        val result = pushNotificationRepositoryImpl.reads(userId)
        assert(result.size == 1)
    }

    fun generateUserId(): String = UUID.randomUUID().toString()
}
