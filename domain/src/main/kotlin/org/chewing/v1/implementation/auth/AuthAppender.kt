package org.chewing.v1.implementation.auth

import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.EmailAddress
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.repository.auth.EmailRepository
import org.chewing.v1.repository.auth.LoggedInRepository
import org.chewing.v1.repository.auth.PhoneRepository
import org.springframework.stereotype.Component

@Component
class AuthAppender(
    private val loggedInRepository: LoggedInRepository,
    private val emailRepository: EmailRepository,
    private val phoneRepository: PhoneRepository,
) {
    fun appendLoggedIn(newRefreshToken: RefreshToken, userId: String) {
        loggedInRepository.append(newRefreshToken, userId)
    }

    fun makeCredential(credential: Credential): String {
        return when (credential) {
            is EmailAddress -> emailRepository.appendIfNotExists(credential)
            is PhoneNumber -> phoneRepository.appendIfNotExists(credential)
        }
    }
}
