package org.chewing.v1.model.token

import java.time.LocalDateTime

class RefreshToken private constructor(
    val token: String,
    val expiredAt: LocalDateTime,
) {
    companion object {
        fun of(
            token: String,
            expiredAt: LocalDateTime,
        ): RefreshToken {
            return RefreshToken(
                token = token,
                expiredAt = expiredAt,
            )
        }
    }
}
