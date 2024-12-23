package org.chewing.v1.repository.auth

import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.contact.Email

interface EmailRepository {
    fun appendIfNotExists(emailAddress: EmailAddress): String
    fun read(emailAddress: EmailAddress): Email?
    fun readById(emailId: String): Email?
}
