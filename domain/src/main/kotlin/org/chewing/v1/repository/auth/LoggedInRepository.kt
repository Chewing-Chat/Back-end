package org.chewing.v1.repository.auth

import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserId

interface LoggedInRepository {
    fun remove(refreshToken: String)
    fun append(refreshToken: RefreshToken, userId: UserId)

    fun update(refreshToken: RefreshToken, preRefreshToken: RefreshToken)
    fun read(refreshToken: String, userId: UserId): RefreshToken?
}
