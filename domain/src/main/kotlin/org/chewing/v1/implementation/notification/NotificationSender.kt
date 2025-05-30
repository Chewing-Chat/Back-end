package org.chewing.v1.implementation.notification

import org.chewing.v1.external.ExternalChatNotificationClient
import org.chewing.v1.external.ExternalPushNotificationClient
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class NotificationSender(
    private val externalPushNotificationClient: ExternalPushNotificationClient,
    private val externalChatNotificationClient: ExternalChatNotificationClient,
    private val asyncJobExecutor: AsyncJobExecutor,
) {
    fun sendPushNotification(notificationList: List<Notification>) {
        asyncJobExecutor.executeAsyncJob(notificationList) {
            externalPushNotificationClient.sendPushNotifications(notificationList)
        }
    }

    fun sendChatNotification(chatMessage: ChatMessage, userId: UserId) {
        externalChatNotificationClient.sendMessage(chatMessage, userId)
    }

    fun sendChatNotifications(chatMessage: ChatMessage, userIdList: List<UserId>) {
        asyncJobExecutor.executeAsyncJobs(userIdList) {
            externalChatNotificationClient.sendMessage(chatMessage, it)
        }
    }
}
