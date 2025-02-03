package org.chewing.v1.repository.support

import org.chewing.v1.model.auth.PushToken
import org.springframework.stereotype.Component

@Component
object PushTokenProvider {
    fun buildDeviceNormal(): PushToken.Device {
        return PushToken.Device.of("deviceId", PushToken.Provider.ANDROID)
    }

    fun buildAppTokenNormal(): String {
        return "appToken"
    }
}
