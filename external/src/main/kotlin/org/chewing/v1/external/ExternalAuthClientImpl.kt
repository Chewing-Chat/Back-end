package org.chewing.v1.external

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class ExternalAuthClientImpl(
    private val javaMailSender: JavaMailSender,
) : ExternalAuthClient {

    override fun sendEmail(emailAddress: EmailAddress, verificationCode: String) {
        val message = SimpleMailMessage()
        message.setTo(emailAddress.address)
        message.subject = "Verification Code"
        message.text = "Your verification code is $verificationCode"
        javaMailSender.send(message)
    }

    override fun sendSms(phoneNumber: PhoneNumber, verificationCode: String) {
        println("Sending SMS to $phoneNumber")
    }
}
