package org.chewing.v1.util.handler

import mu.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.socket.WebSocketHandler
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.WebSocketHandlerDecorator
import org.springframework.web.socket.handler.WebSocketHandlerDecoratorFactory
import java.io.EOFException

@Component
class CustomWebSocketHandlerDecoratorFactory : WebSocketHandlerDecoratorFactory {
    private val logger = KotlinLogging.logger {}
    override fun decorate(handler: WebSocketHandler): WebSocketHandler {
        return object : WebSocketHandlerDecorator(handler) {
            @Throws(Exception::class)
            override fun handleTransportError(session: WebSocketSession, exception: Throwable) {
                if (exception is EOFException) {
                    logger.info { "EOFException 발생 - 클라이언트가 비정상 종료했음. sessionId: ${session.id}" }
                    return
                }
                super.handleTransportError(session, exception)
            }
        }
    }
}
