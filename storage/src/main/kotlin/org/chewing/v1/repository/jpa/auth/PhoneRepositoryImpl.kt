package org.chewing.v1.repository.jpa.auth

import org.chewing.v1.jpaentity.auth.PhoneJpaEntity
import org.chewing.v1.jparepository.auth.PhoneJpaRepository
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Phone
import org.chewing.v1.repository.auth.PhoneRepository
import org.springframework.stereotype.Repository

@Repository
internal class PhoneRepositoryImpl(
    private val phoneJpaRepository: PhoneJpaRepository,
) : PhoneRepository {
    override fun appendIfNotExists(phoneNumber: PhoneNumber): String = phoneJpaRepository.findByNumberAndCountryCode(
        phoneNumber.number,
        phoneNumber.countryCode,
    ).map {
        it.updateVerificationCode()
        phoneJpaRepository.save(it)
        it.getVerifiedNumber()
    }.orElseGet {
        val newPhoneEntity = PhoneJpaEntity.generate(phoneNumber)
        phoneJpaRepository.save(newPhoneEntity)
        newPhoneEntity.getVerifiedNumber()
    }

    override fun read(phoneNumber: PhoneNumber): Phone? = phoneJpaRepository.findByNumberAndCountryCode(
        phoneNumber.number,
        phoneNumber.countryCode,
    ).map { it.toPhone() }.orElse(null)

    override fun readById(phoneNumberId: String): Phone? = phoneJpaRepository.findByPhoneNumberId(phoneNumberId).map { it.toPhone() }.orElse(null)
}
