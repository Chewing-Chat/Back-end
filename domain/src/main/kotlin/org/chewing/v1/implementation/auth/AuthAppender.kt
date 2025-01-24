package org.chewing.v1.implementation.auth

import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Component

@Component
class AuthAppender(
    private val loggedInRepository: LoggedInRepository,
    private val externalAuthClient: ExternalAuthClient,
) {
    fun appendLoggedIn(newRefreshToken: RefreshToken, userId: UserId) {
        loggedInRepository.append(newRefreshToken, userId)
    }

    fun appendVerification(contact: Contact, verificationCode: String) {
        when (contact) {
            is PhoneNumber -> {
                externalAuthClient.cacheVerificationCode(contact, verificationCode)
            }
        }
    }
}
