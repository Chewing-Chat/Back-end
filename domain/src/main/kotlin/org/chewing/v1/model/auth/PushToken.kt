package org.chewing.v1.model.auth

import org.chewing.v1.model.user.UserId

class PushToken private constructor(
    val pushTokenId: String,
    val pushToken: String,
    val device: Device,
    val userId: UserId,
) {
    companion object {
        fun of(
            pushTokenId: String,
            fcmToken: String,
            provider: Provider,
            deviceId: String,
            userId: UserId,
        ): PushToken {
            return PushToken(
                pushTokenId = pushTokenId,
                pushToken = fcmToken,
                device = Device.of(deviceId, provider),
                userId = userId,
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

    enum class Provider {
        ANDROID,
        IOS,
    }
}
