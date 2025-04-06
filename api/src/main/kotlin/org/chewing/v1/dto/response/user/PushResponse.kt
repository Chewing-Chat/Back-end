package org.chewing.v1.dto.response.user

import org.chewing.v1.model.auth.PushInfo
import org.chewing.v1.model.notification.NotificationStatus

data class PushResponse(
    val deviceId: String,
    val scheduleStatus: NotificationStatus,
    val chatStatus: NotificationStatus,
) {
    companion object {
        fun of(
            pushInfo: PushInfo,
        ): PushResponse {
            return PushResponse(
                deviceId = pushInfo.device.deviceId,
                scheduleStatus = pushInfo.statusInfo.scheduleStatus,
                chatStatus = pushInfo.statusInfo.chatStatus,
            )
        }
    }
}
