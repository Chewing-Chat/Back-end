package org.chewing.v1.util.interceptor

import mu.KotlinLogging
import org.chewing.v1.implementation.auth.JwtTokenProvider
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.lang.Exception
import java.security.Principal

@Component
class StompHandshakeInterceptor(
    private val jwtTokenProvider: JwtTokenProvider,
) : HandshakeInterceptor {
    private val logger = KotlinLogging.logger { }
    override fun beforeHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        attributes: MutableMap<String, Any>,
    ): Boolean {
        logger.info { "beforeHandshake" }
        val servletRequest = (request as ServletServerHttpRequest).servletRequest
        val token = servletRequest.getHeader("Authorization")?.substringAfter("Bearer ")
        if (token != null) {
            return try {
                val userId = jwtTokenProvider.getUserIdFromToken(token)
                // 사용자 정보를 attributes에 추가할 수 있습니다.
                attributes["user"] = Principal { userId }
                true
            } catch (e: Exception) {
                logger.error("Failed to authenticate user: ${e.message}")
                response.setStatusCode(HttpStatus.UNAUTHORIZED)
                false
            }
        }
        logger.warn("Authorization header missing")
        response.setStatusCode(HttpStatus.UNAUTHORIZED)
        return false
    }

    override fun afterHandshake(
        request: ServerHttpRequest,
        response: ServerHttpResponse,
        wsHandler: WebSocketHandler,
        exception: Exception?,
    ) {
        logger.info { "afterHandshake" }
    }
}
