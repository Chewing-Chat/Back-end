package org.chewing.v1.implementation.auth

import org.chewing.v1.error.*
import org.chewing.v1.external.ExternalAuthClient
import org.chewing.v1.model.auth.Credential
import org.chewing.v1.model.auth.PhoneNumber
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Component

@Component
class AuthReader(
    private val loggedInRepository: LoggedInRepository,
    private val externalAuthClient: ExternalAuthClient,
) {
    fun readVerificationCode(credential: Credential): String = when (credential) {
        is PhoneNumber -> externalAuthClient.readVerificationCode(credential)
            ?: throw AuthorizationException(ErrorCode.EXPIRED_VERIFICATION_CODE)
    }

    fun readRefreshToken(refreshToken: String, userId: String): RefreshToken = loggedInRepository.read(refreshToken, userId) ?: throw AuthorizationException(ErrorCode.INVALID_TOKEN)
}
