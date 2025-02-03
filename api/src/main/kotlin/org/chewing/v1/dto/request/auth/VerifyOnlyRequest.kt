package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.contact.LocalPhoneNumber

data class VerifyOnlyRequest(
    val phoneNumber: String,
    val countryCode: String,
    val verificationCode: String,
) {
    fun toLocalPhoneNumber(): LocalPhoneNumber {
        return LocalPhoneNumber.of(phoneNumber, countryCode)
    }

    fun toVerificationCode(): String = verificationCode
}
