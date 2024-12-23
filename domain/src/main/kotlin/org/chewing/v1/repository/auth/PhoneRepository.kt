package org.chewing.v1.repository.auth

import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Phone

interface PhoneRepository {
    fun appendIfNotExists(phoneNumber: PhoneNumber): String
    fun read(phoneNumber: PhoneNumber): Phone?
    fun readById(phoneNumberId: String): Phone?
}
