package org.chewing.v1.implementation.auth

import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.PhoneNumber
import org.springframework.stereotype.Component

@Component
class AuthSender(
    val externalAuthClient: ExternalAuthClient,
) {

    fun sendVerificationCode(contact: Contact, verificationCode: String) {
        when (contact) {
            is PhoneNumber -> externalAuthClient.sendSms(contact, verificationCode)
        }
    }
}
