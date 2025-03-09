package org.chewing.v1.service.notification

import org.chewing.v1.implementation.friend.friendship.FriendShipReader
import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.implementation.user.UserReader
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.user.AccessStatus
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
            handleMessageNotification(chatMessage, memberId, userId)
        }
    }

    fun handleMessageNotification(chatMessage: ChatMessage, targetUserId: UserId, userId: UserId) {
        // 온라인 상태 확인
        if (!sessionProvider.isOnline(targetUserId)) {
            // 오프라인 유저에게 푸시 알림 전송
            if (targetUserId != userId) {
                val friendShip = friendShipReader.read(targetUserId, userId)
                val user = userReader.read(targetUserId, AccessStatus.ACCESS)
                val pushTokens = userReader.readsPushToken(targetUserId)
                val notificationList =
                    notificationGenerator.generateMessageNotification(friendShip, pushTokens, chatMessage, user)
                notificationSender.sendPushNotification(notificationList)
            }
        } else {
            notificationSender.sendChatNotification(chatMessage, targetUserId)
        }
    }

    fun handleScheduleNotification(targetUserId: UserId, userId: UserId, targetScheduleId: ScheduleId, scheduleAction: ScheduleAction) {
        if (!sessionProvider.isOnline(targetUserId)) {
            if (targetUserId != userId) {
                val friendShip = friendShipReader.read(targetUserId, userId)
                val user = userReader.read(targetUserId, AccessStatus.ACCESS)
                val pushTokens = userReader.readsPushToken(targetUserId)
                val notificationList =
                    notificationGenerator.generateScheduleNotification(friendShip, pushTokens, targetScheduleId, user, scheduleAction)
                notificationSender.sendPushNotification(notificationList)
            }
        }
    }

    fun handleSchedulesNotification(targetUserIds: List<UserId>, userId: UserId, targetScheduleId: ScheduleId, scheduleAction: ScheduleAction) {
        targetUserIds.forEach { memberId ->
            handleScheduleNotification(memberId, userId, targetScheduleId, scheduleAction)
        }
    }
}
