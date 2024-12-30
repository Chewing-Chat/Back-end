package org.chewing.v1.client

import org.chewing.v1.model.auth.PhoneNumber
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component

@Component
class AuthCacheClient(
    private val cacheManager: CacheManager,
) {

    @CachePut(value = ["phoneVerificationCode"], key = "#phoneNumber.toString()")
    fun cacheVerificationCode(phoneNumber: PhoneNumber, verificationCode: String): String {
        return verificationCode
    }

    @CacheEvict(value = ["phoneVerificationCode"], key = "#phoneNumber.toString()")
    fun removeVerificationCode(phoneNumber: PhoneNumber) {
    }

    @Cacheable(value = ["phoneVerificationCode"], key = "#phoneNumber.toString()")
    fun getVerificationCode(phoneNumber: PhoneNumber): String? {
        // 캐시에 값이 없으면 null 반환
        return null
    }
}
