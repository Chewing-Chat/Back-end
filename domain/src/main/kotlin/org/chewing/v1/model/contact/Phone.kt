package org.chewing.v1.model.contact

import org.chewing.v1.model.auth.ValidationCode
import java.time.LocalDateTime

class Phone private constructor(
    val phoneId: String,
    val countryCode: String,
    val number: String,
    override val validationCode: ValidationCode,
) : Contact() {
    companion object {
        fun of(
            phoneId: String,
            country: String,
            number: String,
            authorizedNumber: String,
            expiredTime: LocalDateTime,
        ): Phone = Phone(
            phoneId = phoneId,
            countryCode = country,
            number = number,
            validationCode = ValidationCode.of(
                authorizedNumber,
                expiredTime,
            ),
        )
    }
}
