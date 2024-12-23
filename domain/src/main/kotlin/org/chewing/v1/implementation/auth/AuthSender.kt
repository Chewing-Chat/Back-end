package org.chewing.v1.implementation.auth

import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.springframework.stereotype.Component

@Component
class AuthSender(
    val externalAuthClient: ExternalAuthClient,
) {

    fun sendVerificationCode(credential: Credential, verificationCode: String) {
        when (credential) {
            is PhoneNumber -> externalAuthClient.sendSms(credential, verificationCode)
            is EmailAddress -> externalAuthClient.sendEmail(credential, verificationCode)
        }
    }
}
