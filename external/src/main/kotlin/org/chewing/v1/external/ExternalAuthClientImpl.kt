package org.chewing.v1.external

import org.chewing.v1.model.auth.PhoneNumber
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class ExternalAuthClientImpl(
    private val javaMailSender: JavaMailSender,
) : ExternalAuthClient {

    override fun sendSms(phoneNumber: PhoneNumber, verificationCode: String) {
        println("Sending SMS to $phoneNumber")
    }
}
