package org.chewing.v1.implementation.auth

import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Component

@Component
class AuthRemover(
    private val loggedInRepository: LoggedInRepository,
) {
    fun removeLoginInfo(refreshToken: String) {
        loggedInRepository.remove(refreshToken)
    }
}
