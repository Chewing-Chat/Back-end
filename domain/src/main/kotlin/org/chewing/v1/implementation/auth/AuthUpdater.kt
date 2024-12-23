package org.chewing.v1.implementation.auth

import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Component

@Component
class AuthUpdater(
    private val loggedInRepository: LoggedInRepository,
) {
    fun updateRefreshToken(refreshToken: RefreshToken, preRefreshToken: RefreshToken) {
        loggedInRepository.update(refreshToken, preRefreshToken)
    }
}
