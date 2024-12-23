package org.chewing.v1.model.auth

import org.chewing.v1.model.token.RefreshToken

class JwtToken private constructor(
    val accessToken: String,
    val refreshToken: RefreshToken,
) {
    companion object {
        fun of(accessToken: String, refreshToken: RefreshToken): JwtToken {
            return JwtToken(accessToken, refreshToken)
        }
    }
}
