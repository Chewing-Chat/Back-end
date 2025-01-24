package org.chewing.v1.jparepository.auth

import org.chewing.v1.jpaentity.auth.LoggedInJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

internal interface LoggedInJpaRepository : JpaRepository<LoggedInJpaEntity, String> {
    fun deleteByRefreshToken(refreshToken: String)

    fun findByRefreshToken(refreshToken: String): Optional<LoggedInJpaEntity>

    fun findByRefreshTokenAndUserId(refreshToken: String, userId: String): Optional<LoggedInJpaEntity>
}
