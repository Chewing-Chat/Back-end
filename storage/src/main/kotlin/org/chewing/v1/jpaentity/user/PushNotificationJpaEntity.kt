package org.chewing.v1.jpaentity.user

import jakarta.persistence.*
import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.UserId
import org.chewing.v1.model.user.UserInfo
import java.util.*

@Entity
@Table(
    name = "push_notification",
    schema = "chewing",
    indexes = [
        Index(name = "push_notification_idx_device_provider", columnList = "deviceId, provider"),
        Index(name = "push_notification_idx_user_id", columnList = "userId"),
        Index(name = "push_notification_idx_app_token_user_id", columnList = "appToken, userId"),
    ],
)
internal class PushNotificationJpaEntity(
    @Id
    @Column(name = "push_notification_id")
    private val pushId: String = UUID.randomUUID().toString(),

    private val appToken: String,

    private val deviceId: String,

    @Enumerated(EnumType.STRING)
    private var provider: PushToken.Provider,

    private val userId: String,
) {
    companion object {
        fun generate(
            appToken: String,
            device: PushToken.Device,
            userInfo: UserInfo,
        ): PushNotificationJpaEntity {
            return PushNotificationJpaEntity(
                appToken = appToken,
                deviceId = device.deviceId,
                provider = device.provider,
                userId = userInfo.userId.id,
            )
        }
    }

    fun toPushToken(): PushToken {
        return PushToken.of(
            pushTokenId = pushId,
            fcmToken = appToken,
            deviceId = deviceId,
            provider = provider,
            userId = UserId.of(userId),
        )
    }
}
