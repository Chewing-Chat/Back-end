package org.chewing.v1.util.interceptor

import mu.KotlinLogging
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.util.security.JwtTokenUtil
import org.springframework.messaging.Message
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.ChannelInterceptor
import org.springframework.messaging.support.MessageHeaderAccessor
import org.springframework.stereotype.Component
import java.security.Principal

@Component
class StompChannelInterceptor(
    private val jwtTokenUtil: JwtTokenUtil,
) : ChannelInterceptor {
    private val logger = KotlinLogging.logger { }

    override fun preSend(message: Message<*>, channel: MessageChannel): Message<*>? {
        val accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor::class.java)
        if (accessor?.command == StompCommand.CONNECT) {
            val token = accessor.getFirstNativeHeader("Authorization")
            if (token.isNullOrEmpty()) {
                logger.warn { "Authentication token missing in CONNECT frame" }
                throw AuthorizationException(ErrorCode.NOT_AUTHORIZED)
            }
            try {
                val cleanToken = jwtTokenUtil.cleanedToken(token)
                val userId = jwtTokenUtil.getUserIdFromToken(cleanToken).id
                accessor.user = Principal { userId }
                logger.info { "User authenticated: $userId" }
            } catch (e: Exception) {
                logger.error { "Authentication failed: ${e.message}" }
                throw AuthorizationException(ErrorCode.NOT_AUTHORIZED)
            }
        }
        return message
    }
}
