package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.auth.PushToken

data class LoginRequest(
    val phoneNumber: String,
    val countryCode: String,
    val password: String,
    val deviceId: String,
    val provider: String,
    val appToken: String,
) {
    fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    fun toPassword(): String = password
    fun toDevice(): PushToken.Device {
        return PushToken.Device.of(deviceId, PushToken.Provider.valueOf(provider.uppercase()))
    }
    fun toAppToken(): String {
        return appToken
    }
}
