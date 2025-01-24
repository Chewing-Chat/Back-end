package org.chewing.v1.model.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.UserInfo

class Notification private constructor(
    val userInfo: UserInfo,
    val pushToken: PushToken,
    val type: NotificationType,
    val targetId: String,
    val content: String,
) {
    companion object {
        fun of(
            userInfo: UserInfo,
            pushToken: PushToken,
            type: NotificationType,
            targetId: String,
            content: String,
        ): Notification {
            return Notification(
                userInfo = userInfo,
                pushToken = pushToken,
                type = type,
                targetId = targetId,
                content = content,
            )
        }
    }
}
