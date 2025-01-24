package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PushToken
import org.chewing.v1.model.contact.LocalPhoneNumber

class SignUpRequest {

    data class Phone(
        val userName: String,
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
        fun toLocalPhoneNumber(): LocalPhoneNumber {
            return LocalPhoneNumber.of(phoneNumber, countryCode)
        }

        fun toUserName(): String {
            return userName
        }
    }
    data class Password(
        val password: String = "",
    )
}
