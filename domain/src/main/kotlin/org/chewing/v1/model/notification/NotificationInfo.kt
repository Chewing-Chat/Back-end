package org.chewing.v1.model.notification

import org.chewing.v1.model.friend.FriendShip
import org.chewing.v1.model.user.UserInfo

class NotificationInfo(
    val friendShip: FriendShip,
    val user: UserInfo,
    val pushInfos: List<PushInfo>,
) {
    companion object {
        fun of(friendShip: FriendShip, user: UserInfo, pushInfos: List<PushInfo>): NotificationInfo {
            return NotificationInfo(friendShip, user, pushInfos)
        }
    }
}
