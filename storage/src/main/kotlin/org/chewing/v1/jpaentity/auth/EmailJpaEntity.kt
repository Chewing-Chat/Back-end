package org.chewing.v1.jpaentity.auth

import jakarta.persistence.*
import org.chewing.v1.jpaentity.common.BaseEntity
import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.contact.Email
import org.hibernate.annotations.DynamicInsert
import java.time.LocalDateTime
import java.util.*

@Entity
@DynamicInsert
@Table(name = "email", schema = "chewing")
internal class EmailJpaEntity(
    @Id
    private val emailId: String = UUID.randomUUID().toString(),

    private var address: String,

    private var expiredAt: LocalDateTime = LocalDateTime.now().plusMinutes(5),

    private var authorizedNumber: String = (100000 + Random().nextInt(900000)).toString(),
) : BaseEntity() {
    companion object {
        fun fromEmail(email: Email): EmailJpaEntity {
            return EmailJpaEntity(
                emailId = email.emailId,
                address = email.emailAddress,
            )
        }

        fun generate(email: EmailAddress): EmailJpaEntity {
            return EmailJpaEntity(
                address = email.address,
            )
        }
    }
    fun toEmail(): Email {
        return Email.of(
            emailId = emailId,
            emailAddress = address,
            authorizedNumber = authorizedNumber,
            expiredTime = expiredAt,
        )
    }

    fun getAuthorizedNumber(): String {
        return authorizedNumber
    }

    fun updateVerificationCode() {
        authorizedNumber = (100000 + Random().nextInt(900000)).toString()
        expiredAt = LocalDateTime.now().plusMinutes(5)
    }
}
