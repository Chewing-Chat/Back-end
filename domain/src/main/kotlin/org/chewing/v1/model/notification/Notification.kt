package org.chewing.v1.model.notification

import org.chewing.v1.model.friend.FriendShip

class Notification private constructor(
    val friendShip: FriendShip,
    val pushInfo: PushInfo,
    val type: NotificationType,
    val targetId: String,
    val content: String,
    val profileImage: String,
) {
    companion object {
        fun of(
            friendShip: FriendShip,
            pushInfo: PushInfo,
            type: NotificationType,
            targetId: String,
            content: String,
            profileImage: String,
        ): Notification {
            return Notification(
                friendShip = friendShip,
                pushInfo = pushInfo,
                type = type,
                targetId = targetId,
                content = content,
                profileImage = profileImage,
            )
        }
    }
}
