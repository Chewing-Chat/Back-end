package org.chewing.v1.model.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.user.User

class Notification private constructor(
    val user: User,
    val pushToken: PushToken,
    val type: NotificationType,
    val targetId: String,
    val content: String,
) {
    companion object {
        fun of(
            user: User,
            pushToken: PushToken,
            type: NotificationType,
            targetId: String,
            content: String,
        ): Notification {
            return Notification(
                user = user,
                pushToken = pushToken,
                type = type,
                targetId = targetId,
                content = content,
            )
        }
    }
}
