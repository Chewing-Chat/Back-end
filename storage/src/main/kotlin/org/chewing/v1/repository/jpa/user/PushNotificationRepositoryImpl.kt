package org.chewing.v1.repository.jpa.user

import org.chewing.v1.jpaentity.user.PushNotificationJpaEntity
import org.chewing.v1.jparepository.user.PushNotificationJpaRepository
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.User
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

    override fun append(device: PushToken.Device, appToken: String, user: User) {
        pushNotificationJpaRepository.save(PushNotificationJpaEntity.generate(appToken, device, user))
    }
    override fun reads(userId: String): List<PushToken> {
        val pushNotifications = pushNotificationJpaRepository.findAllByUserId(userId)
        return pushNotifications.map { it.toPushToken() }
    }
}
