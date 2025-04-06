package org.chewing.v1.implementation.notification

import org.chewing.v1.implementation.friend.friendship.FriendShipReader
import org.chewing.v1.implementation.user.UserReader
import org.chewing.v1.model.notification.NotificationInfo
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Component

@Component
class NotificationProducer(
    private val friendShipReader: FriendShipReader,
    private val userReader: UserReader,
) {
    fun produceNotificationInfo(
        userId: UserId,
        targetUserId: UserId,
    ): NotificationInfo {
        val user = userReader.read(userId, AccessStatus.ACCESS)
        val friendShip = friendShipReader.readByRelation(targetUserId, userId)
        val pushTokens = userReader.readPushTokens(targetUserId)
        return NotificationInfo.of(
            friendShip = friendShip,
            user = user,
            pushInfos = pushTokens,
        )
    }

    fun produceNotificationInfos(
        userId: UserId,
        targetUserIds: List<UserId>,
    ): List<NotificationInfo> {
        val user = userReader.read(userId, AccessStatus.ACCESS)
        val friendShips = friendShipReader.readsByRelation(targetUserIds, userId)
            .associateBy { it.userId }
        val pushTokens = userReader.readsPushTokens(targetUserIds)
            .groupBy { it.userId }

        return targetUserIds.mapNotNull { targetUserId ->
            val friendShip = friendShips[targetUserId]
            val tokens = pushTokens[targetUserId]
            if (friendShip != null && tokens != null) {
                NotificationInfo.of(
                    friendShip = friendShip,
                    user = user,
                    pushInfos = tokens,
                )
            } else {
                null
            }
        }
    }
}
