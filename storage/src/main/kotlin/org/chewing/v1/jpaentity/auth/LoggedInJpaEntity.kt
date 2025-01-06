package org.chewing.v1.jpaentity.auth

import jakarta.persistence.*
import org.chewing.v1.model.token.RefreshToken
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(name = "logged_in", schema = "chewing")
internal class LoggedInJpaEntity(
    @Id
    private val loggedInId: String = UUID.randomUUID().toString(),

    private var refreshToken: String,

    private val userId: String,

    private var expiredAt: LocalDateTime,
) {
    companion object {
        fun generate(refreshToken: RefreshToken, userId: String): LoggedInJpaEntity {
            return LoggedInJpaEntity(
                refreshToken = refreshToken.token,
                userId = userId,
                expiredAt = refreshToken.expiredAt,
            )
        }
    }

    fun toRefreshToken(): RefreshToken {
        return RefreshToken.of(refreshToken, expiredAt)
    }

    fun toLoggedInId(): String {
        return loggedInId
    }

    fun updateRefreshToken(refreshToken: RefreshToken) {
        this.refreshToken = refreshToken.token
        this.expiredAt = refreshToken.expiredAt
    }
}
