package org.chewing.v1.service.notification

import org.chewing.v1.implementation.notification.NotificationGenerator
import org.chewing.v1.implementation.notification.NotificationProducer
import org.chewing.v1.implementation.notification.NotificationSender
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.notification.PushInfo
import org.chewing.v1.model.schedule.ScheduleAction
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.user.UserId
import org.springframework.stereotype.Service

@Service
class NotificationService(
    private val notificationGenerator: NotificationGenerator,
    private val notificationSender: NotificationSender,
    private val sessionProvider: SessionProvider,
    private val notificationProducer: NotificationProducer,
) {
    fun handleMessagesNotification(chatMessage: ChatMessage, targetUserIds: List<UserId>, userId: UserId) {
        val (onlineUserIds, offlineUserIdsOriginal) = targetUserIds.partition { sessionProvider.isOnline(it) }
        // 오프라인 사용자 목록에서 자기 자신 제거
        val offlineUserIds = offlineUserIdsOriginal.filter { it != userId }

        if (onlineUserIds.isNotEmpty()) {
            notificationSender.sendChatNotifications(chatMessage, onlineUserIds)
        }

        if (offlineUserIds.isNotEmpty()) {
            val notificationInfos = notificationProducer.produceNotificationInfos(userId, offlineUserIds, PushInfo.PushTarget.CHAT)
            val notificationList = notificationGenerator.generateMessageNotifications(notificationInfos, chatMessage)
            notificationSender.sendPushNotification(notificationList)
        }
    }

    fun handleMessageNotification(chatMessage: ChatMessage, targetUserId: UserId, userId: UserId) {
        // 온라인 상태 확인
        if (!sessionProvider.isOnline(targetUserId)) {
            // 오프라인 유저에게 푸시 알림 전송 자기 자신은 제외
            if (targetUserId != userId) {
                val notificationInfo = notificationProducer.produceNotificationInfo(userId, targetUserId, PushInfo.PushTarget.CHAT)
                val notifications = notificationGenerator.generateMessageNotification(notificationInfo, chatMessage)
                notificationSender.sendPushNotification(notifications)
            }
        } else {
            notificationSender.sendChatNotification(chatMessage, targetUserId)
        }
    }

    fun handleSchedulesNotification(
        targetUserIds: List<UserId>,
        userId: UserId,
        targetScheduleId: ScheduleId,
        scheduleAction: ScheduleAction,
    ) {
        val offlineUserIds = targetUserIds.filter { !sessionProvider.isOnline(it) }
        if (offlineUserIds.isNotEmpty()) {
            val notificationInfos = notificationProducer.produceNotificationInfos(userId, targetUserIds, PushInfo.PushTarget.SCHEDULE)
            val notificationList = notificationGenerator.generateScheduleNotifications(notificationInfos, targetScheduleId, scheduleAction)
            notificationSender.sendPushNotification(notificationList)
        }
    }
}
