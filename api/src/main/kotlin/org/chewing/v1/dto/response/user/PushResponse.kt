package org.chewing.v1.dto.response.user

import org.chewing.v1.model.auth.PushInfo

data class PushResponse(
    val deviceId: String,
    val scheduleStatus: String,
    val chatStatus: String,
) {
    companion object {
        fun of(
            pushInfo: PushInfo,
        ): PushResponse {
            return PushResponse(
                deviceId = pushInfo.device.deviceId,
                scheduleStatus = pushInfo.statusInfo.scheduleStatus.name.lowercase(),
                chatStatus = pushInfo.statusInfo.chatStatus.name.lowercase(),
            )
        }
    }
}
