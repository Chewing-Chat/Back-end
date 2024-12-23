package org.chewing.v1.model.contact

import org.chewing.v1.model.auth.ValidationCode
import java.time.LocalDateTime

class Email private constructor(
    val emailId: String,
    val emailAddress: String,
    override val validationCode: ValidationCode,
) : Contact() {
    companion object {
        fun of(
            emailId: String,
            emailAddress: String,
            authorizedNumber: String,
            expiredTime: LocalDateTime,
        ): Email = Email(
            emailId = emailId,
            emailAddress = emailAddress,
            validationCode = ValidationCode.of(authorizedNumber, expiredTime),
        )
    }
}
