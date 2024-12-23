package org.chewing.v1.repository.support

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.contact.Email
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
object EmailProvider {
    fun buildNormalAddress(emailAddress: String): EmailAddress = EmailAddress.of(emailAddress)
    fun buildWrongEmailAddress(): EmailAddress = EmailAddress.of("wrong")
    fun buildNormal(): Email = Email.of(UUID.randomUUID().toString(), "normal", "normal", LocalDateTime.now())
    fun buildNew(): Email = Email.of(UUID.randomUUID().toString(), "new", "new", LocalDateTime.now())
}
