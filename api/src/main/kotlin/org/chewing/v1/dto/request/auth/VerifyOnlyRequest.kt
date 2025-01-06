package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.PhoneNumber

data class VerifyOnlyRequest(
    val phoneNumber: String,
    val countryCode: String,
    val verificationCode: String,
) {
    fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    fun toVerificationCode(): String = verificationCode
}
