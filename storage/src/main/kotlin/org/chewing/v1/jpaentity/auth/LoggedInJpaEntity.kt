package org.chewing.v1.jpaentity.auth

import jakarta.persistence.*
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.model.user.UserId
import java.time.LocalDateTime
import java.util.UUID

@Entity
@Table(
    name = "logged_in",
    schema = "chewing",
    indexes = [
        Index(name = "logged_in_idx_refresh_token", columnList = "refreshToken"),
        Index(name = "logged_in_idx_refresh_token_user", columnList = "refreshToken, userId"),
    ],
)
internal class LoggedInJpaEntity(
    @Id
    private val loggedInId: String = UUID.randomUUID().toString(),

    private var refreshToken: String,

    private val userId: String,

    private var expiredAt: LocalDateTime,
) {
    companion object {
        fun generate(refreshToken: RefreshToken, userId: UserId): LoggedInJpaEntity {
            return LoggedInJpaEntity(
                refreshToken = refreshToken.token,
                userId = userId.id,
                expiredAt = refreshToken.expiredAt,
            )
        }
    }

    fun toRefreshToken(): RefreshToken {
        return RefreshToken.of(refreshToken, expiredAt)
    }

    fun updateRefreshToken(refreshToken: RefreshToken) {
        this.refreshToken = refreshToken.token
        this.expiredAt = refreshToken.expiredAt
    }
}
