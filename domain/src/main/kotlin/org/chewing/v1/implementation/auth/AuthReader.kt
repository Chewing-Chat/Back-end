package org.chewing.v1.implementation.auth

import org.chewing.v1.error.*
import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.contact.Contact
import org.chewing.v1.model.contact.PhoneNumber
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserId
import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Component

@Component
class AuthReader(
    private val loggedInRepository: LoggedInRepository,
    private val externalAuthClient: ExternalAuthClient,
) {
    fun readVerificationCode(contact: Contact): String = when (contact) {
        is PhoneNumber -> externalAuthClient.readVerificationCode(contact)
            ?: throw AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)
    }

    fun readLoginInfo(refreshToken: String, userId: UserId): RefreshToken = loggedInRepository.read(refreshToken, userId) ?: throw AuthorizationException(ErrorCode.INVALID_TOKEN)
}
