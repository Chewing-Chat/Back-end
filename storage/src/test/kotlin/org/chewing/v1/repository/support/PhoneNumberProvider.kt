package org.chewing.v1.repository.support

import org.chewing.v1.model.auth.PhoneNumber
import java.util.UUID

object PhoneNumberProvider {
    fun buildPhoneNumber(): PhoneNumber = PhoneNumber.of("82", UUID.randomUUID().toString().substring(0, 10))
}
