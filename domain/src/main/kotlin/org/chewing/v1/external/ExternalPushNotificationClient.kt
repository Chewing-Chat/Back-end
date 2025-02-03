package org.chewing.v1.external

import org.chewing.v1.model.notification.Notification

interface ExternalPushNotificationClient {
    suspend fun sendFcmNotification(notification: Notification)
}
