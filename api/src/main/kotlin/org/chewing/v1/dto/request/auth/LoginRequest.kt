package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PushInfo
import org.chewing.v1.model.contact.LocalPhoneNumber

data class LoginRequest(
    val phoneNumber: String,
    val countryCode: String,
    val password: String,
    val deviceId: String,
    val provider: String,
    val appToken: String,
) {
    fun toLocalPhoneNumber(): LocalPhoneNumber {
        return LocalPhoneNumber.of(phoneNumber, countryCode)
    }

    fun toPassword(): String = password
    fun toDevice(): PushInfo.Device {
        return PushInfo.Device.of(deviceId, PushInfo.Provider.valueOf(provider.uppercase()))
    }
    fun toAppToken(): String {
        return appToken
    }
}
