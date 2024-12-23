package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber

class VerificationRequest {
    data class Email(
        val email: String,
    ) {
        fun toEmailAddress(): EmailAddress = EmailAddress.of(email)
    }

    data class Phone(
        val phoneNumber: String,
        val countryCode: String,
    ) {
        fun toPhoneNumber(): PhoneNumber = PhoneNumber.of(countryCode, phoneNumber)
    }
}
