package org.chewing.v1.repository.support

import org.chewing.v1.model.notification.PushInfo
import org.springframework.stereotype.Component

@Component
object PushTokenProvider {
    fun buildDeviceNormal(): PushInfo.Device {
        return PushInfo.Device.of("deviceId", PushInfo.Provider.ANDROID)
    }

    fun buildAppTokenNormal(): String {
        return "appToken"
    }

    fun buildAppTokenNew(): String {
        return "appTokenNew"
    }
}
