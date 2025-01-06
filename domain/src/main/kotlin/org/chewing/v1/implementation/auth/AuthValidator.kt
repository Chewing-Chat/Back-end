package org.chewing.v1.implementation.auth

import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthValidator() {
    fun validateVerifyCode(existsVerificationCode: String, verificationCode: String) {
        if (existsVerificationCode != verificationCode) {
            throw AuthorizationException(ErrorCode.WRONG_VALIDATE_CODE)
        }
    }

    fun validatePassword(sourcePassword: String, targetPassword: String) {
        val passwordEncoder = BCryptPasswordEncoder()
        if (!passwordEncoder.matches(sourcePassword, targetPassword)) {
            throw AuthorizationException(ErrorCode.WRONG_PASSWORD)
        }
    }
}
