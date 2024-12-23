package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.auth.PhoneJpaRepository
import org.chewing.v1.repository.jpa.auth.PhoneRepositoryImpl
import org.chewing.v1.repository.support.JpaDataGenerator
import org.chewing.v1.repository.support.PhoneProvider
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

class PhoneRepositoryTest : JpaContextTest() {
    @Autowired
    private lateinit var phoneJpaRepository: PhoneJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var phoneRepositoryImpl: PhoneRepositoryImpl

    @Test
    fun `전화번호 조회에 성공해야 한다`() {
        val phoneNumber = PhoneProvider.buildNormalPhoneNumber(generatePhoneNumber())
        jpaDataGenerator.phoneEntityData(phoneNumber)

        val result = phoneRepositoryImpl.read(phoneNumber)

        assert(result != null)
        assert(result!!.number == phoneNumber.number)
        assert(result.countryCode == phoneNumber.countryCode)
    }

    @Test
    fun `전화번호 조회에 실패해야 한다`() {
        val phoneNumber = PhoneProvider.buildNormalPhoneNumber(generatePhoneNumber())
        jpaDataGenerator.phoneEntityData(phoneNumber)

        val wrongPhoneNumber = PhoneProvider.buildWrongPhoneNumber()

        val result = phoneRepositoryImpl.read(wrongPhoneNumber)

        assert(result == null)
    }

    @Test
    fun `전화번호가 존재 한다면 저장하지 않는다`() {
        val phoneNumber = PhoneProvider.buildNormalPhoneNumber(generatePhoneNumber())
        val oldPhone = jpaDataGenerator.phoneEntityData(phoneNumber)

        phoneRepositoryImpl.appendIfNotExists(phoneNumber)
        val result = phoneJpaRepository.findByNumberAndCountryCode(phoneNumber.number, phoneNumber.countryCode)

        assert(result.isPresent)
        assert(result.get().toPhone().phoneId == oldPhone.phoneId)
    }

    @Test
    fun `전화번호가 존재하지 않는다면 저장한다`() {
        val phoneNumber = PhoneProvider.buildNormalPhoneNumber(generatePhoneNumber())
        val verifiedNumber = phoneRepositoryImpl.appendIfNotExists(phoneNumber)
        print(verifiedNumber)
        assert(verifiedNumber.matches(Regex("\\d{6}"))) {
            "인증 번호가 6자리 숫자여야 함"
        }
        val result = phoneJpaRepository.findByNumberAndCountryCode(phoneNumber.number, phoneNumber.countryCode)
        assert(result.isPresent)
        assert(result.get().toPhone().number == phoneNumber.number)
        assert(result.get().toPhone().countryCode == phoneNumber.countryCode)
    }

    @Test
    fun `전화번호를 전화번호 아이디로 읽는다`() {
        val phoneNumber = PhoneProvider.buildNormalPhoneNumber(generatePhoneNumber())
        val phone = jpaDataGenerator.phoneEntityData(phoneNumber)

        val result = phoneRepositoryImpl.readById(phone.phoneId)

        assert(result != null)
        assert(result!!.number == phoneNumber.number)
        assert(result.countryCode == phoneNumber.countryCode)
    }

    fun generatePhoneNumber(): String = UUID.randomUUID().toString()
}
