package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber

class VerificationCheckRequest {
    data class Email(
        val email: String,
        val verificationCode: String,
    ) {
        fun toEmailAddress(): EmailAddress = EmailAddress.of(email)

        fun toVerificationCode(): String = verificationCode
    }
    data class Phone(
        val phoneNumber: String,
        val countryCode: String,
        val verificationCode: String,
    ) {
        fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(phoneNumber, countryCode)
        fun toVerificationCode(): String = verificationCode
    }
}
