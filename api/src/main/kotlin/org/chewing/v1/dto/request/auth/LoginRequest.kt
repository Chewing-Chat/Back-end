package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.auth.PushToken

class LoginRequest {

    data class Phone(
        val phoneNumber: String,
        val countryCode: String,
        val verificationCode: String,
        val deviceId: String,
        val provider: String,
        val appToken: String,
    ) {
        fun toDevice(): PushToken.Device {
            return PushToken.Device.of(deviceId, PushToken.Provider.valueOf(provider.uppercase()))
        }

        fun toAppToken(): String {
            return appToken
        }

        fun toVerificationCode(): String {
            return verificationCode
        }
        fun toPhoneNumber(): PhoneNumber {
            return PhoneNumber.of(countryCode, phoneNumber)
        }
    }
}
