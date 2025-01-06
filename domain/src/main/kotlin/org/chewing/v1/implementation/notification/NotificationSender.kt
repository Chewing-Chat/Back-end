package org.chewing.v1.implementation.notification

import org.chewing.v1.external.ExternalChatNotificationClient
import org.chewing.v1.external.ExternalPushNotificationClient
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.notification.Notification
import org.chewing.v1.util.AsyncJobExecutor
import org.springframework.stereotype.Component

@Component
class NotificationSender(
    private val externalPushNotificationClient: ExternalPushNotificationClient,
    private val externalChatNotificationClient: ExternalChatNotificationClient,
    private val asyncJobExecutor: AsyncJobExecutor,
) {
    fun sendPushNotification(notificationList: List<Notification>) {
        asyncJobExecutor.executeAsyncJobs(notificationList) { notification ->
            externalPushNotificationClient.sendFcmNotification(notification)
        }
    }
    fun sendChatNotification(chatMessage: ChatMessage, userId: String) {
        externalChatNotificationClient.sendMessage(chatMessage, userId)
    }
}
