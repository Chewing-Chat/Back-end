package org.chewing.v1.implementation.auth

import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.springframework.stereotype.Component

@Component
class AuthSender(
    val externalAuthClient: ExternalAuthClient,
) {

    fun sendVerificationCode(localPhoneNumber: LocalPhoneNumber, verificationCode: String) {
        externalAuthClient.sendSms(localPhoneNumber, verificationCode)
    }
}
