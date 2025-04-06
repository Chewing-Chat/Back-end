package org.chewing.v1.dto.request.user

import org.chewing.v1.model.notification.NotificationStatus

class UserRequest {
    data class UpdateStatusMessage(
        val statusMessage: String,
    ) {
        fun toStatusMessage(): String = statusMessage
    }
    data class UpdateNotification(
        val status: Boolean,
        val deviceId: String,
    ) {
        fun toNotification(): NotificationStatus {
            return if (status) {
                NotificationStatus.ALLOWED
            } else {
                NotificationStatus.NOT_ALLOWED
            }
        }
        fun toDeviceId(): String = deviceId
    }
}
