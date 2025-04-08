package org.chewing.v1.repository.user

import org.chewing.v1.model.notification.PushInfo
import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.notification.PushInfo.PushTarget
import org.chewing.v1.model.user.UserInfo
import org.chewing.v1.model.user.UserId

interface PushNotificationRepository {
    fun remove(device: PushInfo.Device)
    fun append(device: PushInfo.Device, appToken: String, userInfo: UserInfo)
    fun readAll(userId: UserId, target: PushTarget): List<PushInfo>
    fun readsAll(userIds: List<UserId>, target: PushTarget): List<PushInfo>
    fun updateChatStatus(
        userId: UserId,
        deviceId: String,
        status: NotificationStatus,
    )
    fun updateScheduleStatus(
        userId: UserId,
        deviceId: String,
        status: NotificationStatus,
    )
    fun read(userId: UserId, deviceId: String): PushInfo?
}
