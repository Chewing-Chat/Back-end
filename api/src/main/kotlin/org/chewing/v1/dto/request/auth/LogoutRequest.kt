package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.notification.PushInfo
import kotlin.text.uppercase

data class LogoutRequest(
    val deviceId: String,
    val provider: String,
) {
    fun toDevice(): PushInfo.Device {
        return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.uppercase()))
    }
}
