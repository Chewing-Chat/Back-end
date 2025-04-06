package org.chewing.v1.dto.request.user

import org.chewing.v1.model.notification.NotificationStatus

class UserRequest {
    data class UpdateStatusMessage(
        val statusMessage: String,
    ) {
        fun toStatusMessage(): String = statusMessage
    }
    data class UpdateNotification(
        val notification: Boolean,
        val deviceId: String,
    ) {
        fun toNotification(): NotificationStatus {
            return if (notification) {
                NotificationStatus.ALLOWED
            } else {
                NotificationStatus.NOT_ALLOWED
            }
        }
        fun toDeviceId(): String = deviceId
    }
}
