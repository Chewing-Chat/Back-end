package org.chewing.v1.model.auth

import java.time.LocalDateTime
import java.util.*

class ValidationCode(
    val code: String,
    val expiredAt: LocalDateTime,
) {
    companion object {
        fun of(code: String, expiredAt: LocalDateTime): ValidationCode {
            return ValidationCode(
                code = code,
                expiredAt = expiredAt,
            )
        }
    }

    fun validateCode(code: String): Boolean {
        return this.code == code
    }

    fun validateExpired(): Boolean {
        return expiredAt.isBefore(LocalDateTime.now())
    }
}
