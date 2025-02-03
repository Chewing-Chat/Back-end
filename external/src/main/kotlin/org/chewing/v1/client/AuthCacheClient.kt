package org.chewing.v1.client

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import mu.KotlinLogging
import org.chewing.v1.model.contact.PhoneNumber
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class AuthCacheClient {

    private val logger = KotlinLogging.logger {}

    private val cache: Cache<String, String> = Caffeine.newBuilder()
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .maximumSize(1000)
        .recordStats()
        .build()

    fun cacheVerificationCode(phoneNumber: PhoneNumber, verificationCode: String): String {
        logger.info { "Cache verification code: $verificationCode" }
        cache.put(phoneNumber.e164PhoneNumber, verificationCode)
        return verificationCode
    }

    fun removeVerificationCode(phoneNumber: PhoneNumber) {
        cache.invalidate(phoneNumber.e164PhoneNumber)
    }

    fun getVerificationCode(phoneNumber: PhoneNumber): String? {
        return cache.getIfPresent(phoneNumber.e164PhoneNumber)
    }
}
