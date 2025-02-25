package org.chewing.v1.external

import org.chewing.v1.client.AuthCacheClient
import org.chewing.v1.client.SmsClient
import org.chewing.v1.dto.SmsMessageDto
import org.chewing.v1.model.contact.LocalPhoneNumber
import org.chewing.v1.model.contact.PhoneNumber
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class ExternalAuthClientImpl(
    private val authCacheClient: AuthCacheClient,
    private val smsClient: SmsClient,
    @Value("\${ncp.sms.phoneNumber}")
    private val fromPhoneNumber: String,
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

    override fun sendSms(localPhoneNumber: LocalPhoneNumber, verificationCode: String) {
        val dto = SmsMessageDto.from(localPhoneNumber, fromPhoneNumber, verificationCode)
        smsClient.send(dto)
    }
}
