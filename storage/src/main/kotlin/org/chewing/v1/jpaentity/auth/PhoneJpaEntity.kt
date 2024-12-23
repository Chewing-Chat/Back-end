package org.chewing.v1.jpaentity.auth

import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Phone
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "phone_number", schema = "chewing")
internal class PhoneJpaEntity(
    @Id
    private val phoneNumberId: String = UUID.randomUUID().toString(),
    private var number: String,

    private var countryCode: String,

    private var expiredAt: LocalDateTime = LocalDateTime.now().plusMinutes(5),

    private var authorizedNumber: String = (100000 + Random().nextInt(900000)).toString(),
) : BaseEntity() {
    companion object {
        fun generate(phoneNumber: PhoneNumber): PhoneJpaEntity {
            return PhoneJpaEntity(
                number = phoneNumber.number,
                countryCode = phoneNumber.countryCode,
            )
        }
    }
    fun toPhone(): Phone {
        return Phone.of(
            phoneId = phoneNumberId,
            number = number,
            country = countryCode,
            authorizedNumber = authorizedNumber,
            expiredTime = expiredAt,
        )
    }

    fun getVerifiedNumber(): String {
        return authorizedNumber
    }

    fun updateVerificationCode() {
        authorizedNumber = (100000 + Random().nextInt(900000)).toString()
        expiredAt = LocalDateTime.now().plusMinutes(5)
    }
}
