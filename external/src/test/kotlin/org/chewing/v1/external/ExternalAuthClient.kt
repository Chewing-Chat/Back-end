package org.chewing.v1.external

import org.chewing.v1.TestDataFactory
import org.chewing.v1.client.AuthCacheClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExternalAuthClient() {
    private lateinit var authCacheClient: AuthCacheClient

    @BeforeEach
    fun setUp() {
        authCacheClient = AuthCacheClient()
    }

    @Test
    fun `캐시 저장 및 조회 테스트`() {
        // given
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "123456"

        // when
        authCacheClient.cacheVerificationCode(phoneNumber, verificationCode)
        val result = authCacheClient.getVerificationCode(phoneNumber)

        // then
        assert(verificationCode == result)
    }

    @Test
    fun `캐시 제거 테스트`() {
        // given
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val verificationCode = "123456"

        authCacheClient.cacheVerificationCode(phoneNumber, verificationCode)

        // when
        authCacheClient.removeVerificationCode(phoneNumber)
        val result = authCacheClient.getVerificationCode(phoneNumber)

        // then
        assert(result == null)
    }

    @Test
    fun `캐시 없다면 조회 테스트`() {
        // given
        val phoneNumber = TestDataFactory.createPhoneNumber()
        val wrongPhoneNumber = TestDataFactory.createWrongPhoneNumber()
        val verificationCode = "123456"
        authCacheClient.cacheVerificationCode(phoneNumber, verificationCode)
        // when
        val result = authCacheClient.getVerificationCode(wrongPhoneNumber)

        // then
        assert(result == null)
    }
}
