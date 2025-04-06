package org.chewing.v1.model.auth

import org.chewing.v1.model.notification.NotificationStatus
import org.chewing.v1.model.user.UserId

class PushInfo private constructor(
    val pushId: String,
    val pushToken: String,
    val device: Device,
    val userId: UserId,
    val statusInfo: NotificationStatusInfo,
) {
    companion object {
        fun of(
            pushTokenId: String,
            fcmToken: String,
            provider: Provider,
            deviceId: String,
            userId: UserId,
            chatStatus: NotificationStatus,
            scheduleStatus: NotificationStatus,
        ): PushInfo {
            return PushInfo(
                pushId = pushTokenId,
                pushToken = fcmToken,
                device = Device.of(deviceId, provider),
                userId = userId,
                statusInfo = NotificationStatusInfo(
                    chatStatus = chatStatus,
                    scheduleStatus = scheduleStatus,
                ),
            )
        }
    }

    class Device private constructor(
        val deviceId: String,
        val provider: Provider,
    ) {
        companion object {
            fun of(
                deviceId: String,
                provider: Provider,
            ): Device {
                return Device(
                    deviceId = deviceId,
                    provider = provider,
                )
            }
        }
    }

    data class NotificationStatusInfo(
        val chatStatus: NotificationStatus,
        val scheduleStatus: NotificationStatus,
    )

    enum class Provider {
        ANDROID,
        IOS,
    }

    enum class PushType {
        CHAT,
        SCHEDULE,
    }
}
