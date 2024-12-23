package org.chewing.v1.util.listener

import mu.KotlinLogging
import org.chewing.v1.implementation.session.SessionProvider
import org.springframework.context.event.EventListener
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.SessionConnectedEvent
import org.springframework.web.socket.messaging.SessionDisconnectEvent

@Component
class WebSocketEventListener(
    private val sessionProvider: SessionProvider,
) {
    private val logger = KotlinLogging.logger { }

    @EventListener
    fun handleWebSocketConnectListener(event: SessionConnectedEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = accessor.sessionId ?: return
        val userId = accessor.user?.name ?: return

        sessionProvider.connect(userId, sessionId)
        logger.info("세션 연결됨: userId=$userId, sessionId=$sessionId")
    }

    @EventListener
    fun handleWebSocketDisconnectListener(event: SessionDisconnectEvent) {
        val accessor = StompHeaderAccessor.wrap(event.message)
        val sessionId = accessor.sessionId ?: return
        val userId = accessor.user?.name ?: return

        sessionProvider.unConnect(userId, sessionId)
        logger.info("세션 해제됨: userId=$userId, sessionId=$sessionId")
    }
}
