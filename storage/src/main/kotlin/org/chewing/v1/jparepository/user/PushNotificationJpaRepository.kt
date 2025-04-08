package org.chewing.v1.jparepository.user

import org.chewing.v1.jpaentity.user.PushNotificationJpaEntity
import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.notification.PushInfo
import org.springframework.data.jpa.repository.JpaRepository

internal interface PushNotificationJpaRepository : JpaRepository<PushNotificationJpaEntity, String> {
    fun deleteByDeviceIdAndProvider(deviceId: String, deviceProvider: PushInfo.Provider)
    fun findAllByUserId(userId: String): List<PushNotificationJpaEntity>
    fun findAllByUserIdAndChatStatus(userId: String, chatStatus: NotificationStatus): List<PushNotificationJpaEntity>
    fun findAllByUserIdAndScheduleStatus(userId: String, scheduleStatus: NotificationStatus): List<PushNotificationJpaEntity>
    fun findAllByUserIdIn(userIds: List<String>): List<PushNotificationJpaEntity>
    fun findAllByUserIdInAndChatStatus(userIds: List<String>, chatStatus: NotificationStatus): List<PushNotificationJpaEntity>
    fun findAllByUserIdInAndScheduleStatus(userIds: List<String>, scheduleStatus: NotificationStatus): List<PushNotificationJpaEntity>
    fun findByDeviceIdAndUserId(deviceId: String, userId: String): PushNotificationJpaEntity?
}
