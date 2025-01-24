package org.chewing.v1.implementation.auth

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component
import java.util.Random

@Component
class AuthGenerator {

    fun generateVerificationCode(): String {
        return (100000 + Random().nextInt(900000)).toString()
    }

    fun hashPassword(password: String): String {
        val passwordEncoder = BCryptPasswordEncoder()
        return passwordEncoder.encode(password)
    }
}
