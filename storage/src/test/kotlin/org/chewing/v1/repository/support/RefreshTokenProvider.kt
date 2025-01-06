package org.chewing.v1.repository.support

import org.chewing.v1.model.token.RefreshToken
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.UUID

@Component
object RefreshTokenProvider {
    fun buildNormal(): RefreshToken = RefreshToken.of(UUID.randomUUID().toString(), LocalDateTime.now())
    fun buildNew(): RefreshToken = RefreshToken.of("new", LocalDateTime.now())
}
