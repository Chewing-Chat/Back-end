package org.chewing.v1.model.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.friend.FriendShip

class Notification private constructor(
    val friendShip: FriendShip,
    val pushToken: PushToken,
    val type: NotificationType,
    val targetId: String,
    val content: String,
    val profileImage: String,
) {
    companion object {
        fun of(
            friendShip: FriendShip,
            pushToken: PushToken,
            type: NotificationType,
            targetId: String,
            content: String,
            profileImage: String,
        ): Notification {
            return Notification(
                friendShip = friendShip,
                pushToken = pushToken,
                type = type,
                targetId = targetId,
                content = content,
                profileImage= profileImage
            )
        }
    }
}
