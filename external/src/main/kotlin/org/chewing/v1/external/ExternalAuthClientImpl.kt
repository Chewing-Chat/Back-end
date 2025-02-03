package org.chewing.v1.external

import org.chewing.v1.client.AuthCacheClient
import org.chewing.v1.model.contact.PhoneNumber
import org.springframework.stereotype.Component

@Component
class ExternalAuthClientImpl(
    private val authCacheClient: AuthCacheClient,
) : ExternalAuthClient {

    override fun cacheVerificationCode(phoneNumber: PhoneNumber, verificationCode: String) {
        authCacheClient.cacheVerificationCode(phoneNumber, verificationCode)
    }

    override fun readVerificationCode(phoneNumber: PhoneNumber): String? {
        return authCacheClient.getVerificationCode(phoneNumber)
    }

    override fun deleteVerificationCode(phoneNumber: PhoneNumber) {
        authCacheClient.removeVerificationCode(phoneNumber)
    }

    override fun sendSms(phoneNumber: PhoneNumber, verificationCode: String) {
        println("Sending SMS to $phoneNumber")
    }
}
