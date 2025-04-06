package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.PushNotificationJpaEntity
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.model.auth.PushInfo
import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.user.PushNotificationRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class PushNotificationRepositoryImpl(
    private val pushNotificationJpaRepository: PushNotificationJpaRepository,
) : PushNotificationRepository {
    @Transactional
    override fun remove(device: PushInfo.Device) {
        pushNotificationJpaRepository.deleteByDeviceIdAndProvider(device.deviceId, device.provider)
    }

    override fun append(device: PushInfo.Device, appToken: String, userInfo: UserInfo) {
        val entity = pushNotificationJpaRepository.findByAppTokenAndUserId(appToken, userInfo.userId.id)
        if (entity == null) {
            pushNotificationJpaRepository.save(PushNotificationJpaEntity.generate(appToken, device, userInfo))
        }
    }
    override fun readAll(userId: UserId): List<PushInfo> {
        val pushNotifications = pushNotificationJpaRepository.findAllByUserId(userId.id)
        return pushNotifications.map { it.toPushToken() }
    }

    override fun readsAll(userIds: List<UserId>): List<PushInfo> {
        val pushNotifications = pushNotificationJpaRepository.findAllByUserIdIn(userIds.map { it.id })
        return pushNotifications.map { it.toPushToken() }
    }

    override fun updateChatStatus(
        userId: UserId,
        deviceId: String,
        status: NotificationStatus
    ) {
        pushNotificationJpaRepository.findByDeviceIdAndUserId(
            deviceId = deviceId,
            userId = userId.id,
        )?.let {
            it.updateChatStatus(status)
            pushNotificationJpaRepository.save(it)
        }
    }

    override fun updateScheduleStatus(
        userId: UserId,
        deviceId: String,
        status: NotificationStatus
    ) {
        pushNotificationJpaRepository.findByDeviceIdAndUserId(
            deviceId = deviceId,
            userId = userId.id,
        )?.let {
            it.updateScheduleStatus(status)
            pushNotificationJpaRepository.save(it)
        }
    }

    override fun read(userId: UserId, deviceId: String): PushInfo? {
        return pushNotificationJpaRepository.findByDeviceIdAndUserId(
            deviceId = deviceId,
            userId = userId.id,
        )?.toPushToken()
    }
}
