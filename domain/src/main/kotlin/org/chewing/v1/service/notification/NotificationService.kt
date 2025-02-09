package org.chewing.v1.service.notification

import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.implementation.user.user.UserReader
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val userReader: UserReader,
    private val notificationGenerator: NotificationGenerator,
    private val notificationSender: NotificationSender,
    private val sessionProvider: SessionProvider,
) {
    fun handleMessagesNotification(chatMessage: ChatMessage, targetUserIds: List<UserId>, userId: UserId) {
        val user = userReader.read(userId, AccessStatus.ACCESS)
        targetUserIds.forEach { memberId ->
            // 온라인 상태 확인
            if (!sessionProvider.isOnline(memberId)) {
                // 오프라인 유저에게 푸시 알림 전송
                val pushTokens = userReader.readsPushToken(memberId)
                val notificationList = notificationGenerator.generateMessageNotification(user, pushTokens, chatMessage)
                notificationSender.sendPushNotification(notificationList)
            } else {
                notificationSender.sendChatNotification(chatMessage, memberId)
            }
        }
    }
    fun handleMessageNotification(chatMessage: ChatMessage, targetUserId: UserId, userId: UserId) {
        val user = userReader.read(userId, AccessStatus.ACCESS)
        // 온라인 상태 확인
        if (!sessionProvider.isOnline(targetUserId)) {
            // 오프라인 유저에게 푸시 알림 전송
            val pushTokens = userReader.readsPushToken(targetUserId)
            val notificationList = notificationGenerator.generateMessageNotification(user, pushTokens, chatMessage)
            notificationSender.sendPushNotification(notificationList)
        } else {
            notificationSender.sendChatNotification(chatMessage, targetUserId)
        }
    }
}
