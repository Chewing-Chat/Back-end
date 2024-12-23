package org.chewing.v1.implementation.auth

import org.chewing.v1.error.*
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.ContactType
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.repository.auth.EmailRepository
import org.chewing.v1.repository.auth.LoggedInRepository
import org.chewing.v1.repository.auth.PhoneRepository
import org.springframework.stereotype.Component

@Component
class AuthReader(
    private val phoneRepository: PhoneRepository,
    private val emailRepository: EmailRepository,
    private val loggedInRepository: LoggedInRepository,
) {
    fun readContact(targetContact: Credential): Contact = when (targetContact) {
        is EmailAddress -> emailRepository.read(targetContact)
            ?: throw ConflictException(ErrorCode.WRONG_ACCESS)

        is PhoneNumber -> phoneRepository.read(targetContact)
            ?: throw ConflictException(ErrorCode.WRONG_ACCESS)
    }

    fun readRefreshToken(refreshToken: String, userId: String): RefreshToken = loggedInRepository.read(refreshToken, userId) ?: throw AuthorizationException(ErrorCode.INVALID_TOKEN)

    fun readContactById(contactId: String, contactType: ContactType): Contact? = when (contactType) {
        ContactType.EMAIL -> emailRepository.readById(contactId)
        ContactType.PHONE -> phoneRepository.readById(contactId)
    }
}
