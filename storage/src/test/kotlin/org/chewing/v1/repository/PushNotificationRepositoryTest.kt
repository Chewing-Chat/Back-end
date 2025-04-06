package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.jpa.user.PushNotificationRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.PushTokenProvider
import org.chewing.v1.repository.support.UserProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class PushNotificationRepositoryTest : JpaContextTest() {
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
        val pushInfo = pushNotificationRepositoryImpl.read(userId, device.deviceId)
        assert(pushInfo != null)
        assert(pushInfo!!.device.deviceId == device.deviceId)
        assert(pushInfo.device.provider == device.provider)
        assert(pushInfo.pushToken == appToken)
        assert(pushInfo.userId == userId)
        assert(pushInfo.statusInfo.chatStatus == NotificationStatus.ALLOWED)
        assert(pushInfo.statusInfo.scheduleStatus == NotificationStatus.ALLOWED)
    }

    @Test
    fun `푸시 알림을 위한 정보 저장에 실패 - 이미 존재한다면 저장 하지 않음`() {
        val userId = generateUserId()
        val user = UserProvider.buildNormal(userId)
        val device = PushTokenProvider.buildDeviceNormal()
        val appToken = PushTokenProvider.buildAppTokenNormal()
        pushNotificationRepositoryImpl.append(device, appToken, user)
        pushNotificationRepositoryImpl.append(device, appToken, user)
        assert(pushNotificationJpaRepository.findAllByUserId(userId.id).size == 1)
    }

    @Test
    fun `푸시 알림을 위한 정보 삭제에 성공`() {
        val userId = generateUserId()
        val pushNotification = jpaDataGenerator.pushNotificationData(userId)
        pushNotificationRepositoryImpl.remove(pushNotification.device)
        assert(pushNotificationJpaRepository.findById(pushNotification.pushId).isEmpty)
    }

    @Test
    fun `푸시 알림을 위한 정보 전체 삭제에 성공`() {
        val userId = generateUserId()
        jpaDataGenerator.pushNotificationData(userId)
        val result = pushNotificationRepositoryImpl.readAll(userId)
        assert(result.size == 1)
    }

    @Test
    fun `푸시 채팅 알림 상태 업데이트에 성공`() {
        val userId = generateUserId()
        val pushNotification = jpaDataGenerator.pushNotificationData(userId)
        pushNotificationRepositoryImpl.updateChatStatus(
            userId = userId,
            deviceId = pushNotification.device.deviceId,
            status = NotificationStatus.NOT_ALLOWED,
        )
        assert(pushNotificationJpaRepository.findById(pushNotification.pushId).isPresent)
        assert(pushNotificationJpaRepository.findById(pushNotification.pushId).get().toPushToken().statusInfo.chatStatus == NotificationStatus.NOT_ALLOWED)
    }

    @Test
    fun `푸시 일정 알림 상태 업데이트에 성공`() {
        val userId = generateUserId()
        val pushNotification = jpaDataGenerator.pushNotificationData(userId)
        pushNotificationRepositoryImpl.updateScheduleStatus(
            userId = userId,
            deviceId = pushNotification.device.deviceId,
            status = NotificationStatus.NOT_ALLOWED,
        )
        assert(pushNotificationJpaRepository.findById(pushNotification.pushId).isPresent)
        assert(pushNotificationJpaRepository.findById(pushNotification.pushId).get().toPushToken().statusInfo.scheduleStatus == NotificationStatus.NOT_ALLOWED)
    }

    private fun generateUserId() = UserId.of(UUID.randomUUID().toString())
}
