package org.chewing.v1.util.interceptor

import mu.KotlinLogging
import org.chewing.v1.util.security.JwtTokenUtil
import org.springframework.http.HttpStatus
import org.springframework.http.server.ServerHttpRequest
import org.springframework.http.server.ServerHttpResponse
import org.springframework.http.server.ServletServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.server.HandshakeInterceptor
import java.security.Principal

@Component
class StompHandshakeInterceptor(
    private val jwtTokenUtil: JwtTokenUtil,
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
        val protocols = servletRequest.getHeaders("Sec-WebSocket-Protocol").toList().flatMap { it.split(" ") }
        if (protocols.size == 2) {
            val tokenType = protocols[0]
            val token = protocols[1]
            if (token != null && tokenType == "Bearer") {
                return try {
                    val userId = jwtTokenUtil.getUserIdFromToken(token).id
                    attributes["user"] = Principal { userId }
                    true
                } catch (e: Exception) {
                    logger.error("Failed to authenticate user: ${e.message}")
                    response.setStatusCode(HttpStatus.UNAUTHORIZED)
                    false
                }
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
