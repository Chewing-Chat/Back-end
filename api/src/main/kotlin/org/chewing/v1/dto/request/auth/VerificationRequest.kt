package org.chewing.v1.dto.request.auth

import org.chewing.v1.model.contact.LocalPhoneNumber

class VerificationRequest {
    data class Phone(
        val phoneNumber: String,
        val countryCode: String,
    ) {
        fun toLocalPhoneNumber(): LocalPhoneNumber {
            return LocalPhoneNumber.of(phoneNumber, countryCode)
        }
    }
}
