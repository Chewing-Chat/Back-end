package org.chewing.v1.repository.jpa.auth

import org.chewing.v1.jpaentity.auth.LoggedInJpaEntity
import org.chewing.v1.jparepository.auth.LoggedInJpaRepository
import org.chewing.v1.model.token.RefreshToken
import org.chewing.v1.repository.auth.LoggedInRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
internal class LoggedInRepositoryImpl(
    private val loggedInJpaRepository: LoggedInJpaRepository,
) : LoggedInRepository {
    @Transactional
    override fun remove(refreshToken: String) {
        loggedInJpaRepository.deleteByRefreshToken(refreshToken)
    }

    override fun append(refreshToken: RefreshToken, userId: String) {
        loggedInJpaRepository.save(LoggedInJpaEntity.generate(refreshToken, userId))
    }

    override fun update(refreshToken: RefreshToken, preRefreshToken: RefreshToken) {
        loggedInJpaRepository.findByRefreshToken(preRefreshToken.token).ifPresent {
            it.updateRefreshToken(refreshToken)
            loggedInJpaRepository.save(it)
        }
    }

    override fun read(refreshToken: String, userId: String): RefreshToken? = loggedInJpaRepository.findByRefreshTokenAndUserId(refreshToken, userId).orElse(null).toRefreshToken()
}
