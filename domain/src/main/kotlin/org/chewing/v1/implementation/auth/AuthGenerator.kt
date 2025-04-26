package org.chewing.v1.implementation.auth

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthGenerator {

    fun generateVerificationCode(): String {
//        return (100000 + Random().nextInt(900000)).toString()
        return "000000"
    }

    fun hashPassword(password: String): String {
        val passwordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.encode(password)
    }
}
