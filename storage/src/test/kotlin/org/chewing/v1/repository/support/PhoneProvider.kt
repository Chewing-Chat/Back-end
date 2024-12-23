package org.chewing.v1.repository.support

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Phone
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
object PhoneProvider {
    fun buildNormalPhoneNumber(number: String): PhoneNumber = PhoneNumber.of("normal", number)

    fun buildWrongPhoneNumber(): PhoneNumber = PhoneNumber.of("wrong", "wrong")

    fun buildNormal(): Phone = Phone.of(UUID.randomUUID().toString(), "normal", "normal", "normal", LocalDateTime.now())
}
