package org.chewing.v1.model.notification

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.UserInfo

class NotificationInfo(
    val friendShip: FriendShip,
    val user: UserInfo,
    val pushTokens: List<PushToken>,
) {
    companion object {
        fun of(friendShip: FriendShip, user: UserInfo, pushTokens: List<PushToken>): NotificationInfo {
            return NotificationInfo(friendShip, user, pushTokens)
        }
    }
}
