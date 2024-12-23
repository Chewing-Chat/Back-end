package org.chewing.v1.repository

import org.chewing.v1.config.JpaContextTest
import org.chewing.v1.jparepository.auth.EmailJpaRepository
import org.chewing.v1.repository.jpa.auth.EmailRepositoryImpl
import org.chewing.v1.repository.support.EmailProvider
import org.chewing.v1.repository.support.JpaDataGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.util.UUID

internal class EmailRepositoryTest : JpaContextTest() {

    @Autowired
    private lateinit var emailJpaRepository: EmailJpaRepository

    @Autowired
    private lateinit var jpaDataGenerator: JpaDataGenerator

    @Autowired
    private lateinit var emailRepositoryImpl: EmailRepositoryImpl

    @Test
    fun `이메일 조회에 성공해야 한다`() {
        val address = generateEmailAddress()
        val emailAddress = EmailProvider.buildNormalAddress(address)
        jpaDataGenerator.emailEntityData(emailAddress)

        val result = emailRepositoryImpl.read(emailAddress)

        assert(result != null)
        assert(result!!.emailAddress == emailAddress.address)
    }

    @Test
    fun `이메일 조회에 실패해야 한다`() {
        val address = generateEmailAddress()
        val emailAddress = EmailProvider.buildNormalAddress(address)
        jpaDataGenerator.emailEntityData(emailAddress)

        val wrongEmailAddress = EmailProvider.buildWrongEmailAddress()

        val result = emailRepositoryImpl.read(wrongEmailAddress)

        assert(result == null)
    }

    @Test
    fun `이메일이 존재 한다면 저장하지 않는다`() {
        val address = generateEmailAddress()
        val emailAddress = EmailProvider.buildNormalAddress(address)

        val oldEmail = jpaDataGenerator.emailEntityData(emailAddress)

        val verifiedNumber = emailRepositoryImpl.appendIfNotExists(emailAddress)
        assert(verifiedNumber.matches(Regex("\\d{6}"))) {
            "인증 번호가 6자리 숫자여야 함"
        }
        val result = emailJpaRepository.findByAddress(emailAddress.address)

        assert(result.isPresent)
        assert(result.get().toEmail().emailId == oldEmail.emailId)
        assert(result.get().toEmail().emailAddress == emailAddress.address)
        assert(result.get().toEmail().validationCode.code != oldEmail.validationCode.code)
        assert(result.get().toEmail().validationCode.expiredAt != oldEmail.validationCode.expiredAt)
    }

    @Test
    fun `이메일이 존재하지 않는다면 저장한다`() {
        val address = generateEmailAddress()
        val emailAddress = EmailProvider.buildNormalAddress(address)

        emailRepositoryImpl.appendIfNotExists(emailAddress)

        val result = emailJpaRepository.findByAddress(emailAddress.address)
        assert(result.isPresent)
        assert(result.get().toEmail().emailAddress == emailAddress.address)
    }

    @Test
    fun `이메일을 이메일 아이디로 읽는다`() {
        val address = generateEmailAddress()
        val emailAddress = EmailProvider.buildNormalAddress(address)

        val email = jpaDataGenerator.emailEntityData(emailAddress)

        val result = emailRepositoryImpl.readById(email.emailId)

        assert(result != null)
        assert(result!!.emailId == email.emailId)
    }

    private fun generateEmailAddress() = UUID.randomUUID().toString()
}
