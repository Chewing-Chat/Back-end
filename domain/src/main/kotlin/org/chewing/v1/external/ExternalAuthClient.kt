package org.chewing.v1.external

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber

interface ExternalAuthClient {
    fun sendEmail(emailAddress: EmailAddress, verificationCode: String)
    fun sendSms(phoneNumber: PhoneNumber, verificationCode: String)
}
