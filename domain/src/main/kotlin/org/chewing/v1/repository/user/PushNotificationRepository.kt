package org.chewing.v1.repository.user

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.User
import org.chewing.v1.model.user.UserId

interface PushNotificationRepository {
    fun remove(device: PushToken.Device)
    fun append(device: PushToken.Device, appToken: String, user: User)
    fun reads(userId: UserId): List<PushToken>
}
