package org.chewing.v1.dto

import org.chewing.v1.model.contact.LocalPhoneNumber

data class SmsMessageDto(
    val type: String,
    val contentType: String,
    val countryCode: String,
    val from: String,
    val subject: String,
    val content: String,
    val messages: List<MessageDto>,
) {
    companion object {
        fun from(localPhoneNumber: LocalPhoneNumber, fromPhoneNumber: String, verificationCode: String): SmsMessageDto {
            return SmsMessageDto(
                type = "SMS",
                contentType = "COMM",
                countryCode = localPhoneNumber.countryCode,
                from = fromPhoneNumber,
                subject = "Verification Code",
                content = "Your verification code is $verificationCode",
                messages = listOf(
                    MessageDto(
                        to = localPhoneNumber.number,
                        subject = "Verification Code",
                        content = "Your verification code is $verificationCode",
                    ),
                ),
            )
        }
    }
}
