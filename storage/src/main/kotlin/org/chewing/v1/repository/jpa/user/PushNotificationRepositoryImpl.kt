package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.PushNotificationJpaEntity
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.model.auth.PushToken
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
    override fun remove(device: PushToken.Device) {
        pushNotificationJpaRepository.deleteByDeviceIdAndProvider(device.deviceId, device.provider)
    }

    override fun append(device: PushToken.Device, appToken: String, userInfo: UserInfo) {
        val entity = pushNotificationJpaRepository.findByAppTokenAndUserId(appToken, userInfo.userId.id)
        if (entity == null) {
            pushNotificationJpaRepository.save(PushNotificationJpaEntity.generate(appToken, device, userInfo))
        }
    }
    override fun read(userId: UserId): List<PushToken> {
        val pushNotifications = pushNotificationJpaRepository.findAllByUserId(userId.id)
        return pushNotifications.map { it.toPushToken() }
    }

    override fun reads(userIds: List<UserId>): List<PushToken> {
        val pushNotifications = pushNotificationJpaRepository.findAllByUserIdIn(userIds.map { it.id })
        return pushNotifications.map { it.toPushToken() }
    }
}
