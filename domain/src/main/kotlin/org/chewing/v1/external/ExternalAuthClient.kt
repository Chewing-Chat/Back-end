package org.chewing.v1.external

import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber

interface ExternalAuthClient {
    fun cacheVerificationCode(phoneNumber: PhoneNumber, verificationCode: String)
    fun readVerificationCode(phoneNumber: PhoneNumber): String?
    fun deleteVerificationCode(phoneNumber: PhoneNumber)
    fun sendSms(localPhoneNumber: LocalPhoneNumber, verificationCode: String)
}
