package org.chewing.v1.service.notification

import org.chewing.v1.implementation.friend.friendship.FriendShipReader
import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.implementation.user.user.UserReader
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val userReader: UserReader,
    private val notificationGenerator: NotificationGenerator,
    private val notificationSender: NotificationSender,
    private val sessionProvider: SessionProvider,
    private val friendShipReader: FriendShipReader,
) {
    fun handleMessagesNotification(chatMessage: ChatMessage, targetUserIds: List<UserId>, userId: UserId) {
        targetUserIds.forEach { memberId ->
            // 온라인 상태 확인
            if (!sessionProvider.isOnline(memberId)) {
                // 오프라인 유저에게 푸시 알림 전송
                if (memberId != userId) {
                    val friendShip = friendShipReader.read(memberId, userId)
                    val pushTokens = userReader.readsPushToken(memberId)
                    val notificationList =
                        notificationGenerator.generateMessageNotification(friendShip, pushTokens, chatMessage)
                    notificationSender.sendPushNotification(notificationList)
                }
            } else {
                notificationSender.sendChatNotification(chatMessage, memberId)
            }
        }
    }

    fun handleMessageNotification(chatMessage: ChatMessage, targetUserId: UserId, userId: UserId) {
        // 온라인 상태 확인
        if (!sessionProvider.isOnline(targetUserId)) {
            // 오프라인 유저에게 푸시 알림 전송
            if (targetUserId != userId) {
                val friendShip = friendShipReader.read(targetUserId, userId)
                val pushTokens = userReader.readsPushToken(targetUserId)
                val notificationList =
                    notificationGenerator.generateMessageNotification(friendShip, pushTokens, chatMessage)
                notificationSender.sendPushNotification(notificationList)
            }
        } else {
            notificationSender.sendChatNotification(chatMessage, targetUserId)
        }
    }
}
