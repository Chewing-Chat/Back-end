package org.chewing.v1.util.handler

import com.fasterxml.jackson.databind.ObjectMapper
import mu.KotlinLogging
import org.chewing.v1.error.AuthorizationException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.response.ErrorResponse
import org.springframework.messaging.Message
import org.springframework.messaging.MessageDeliveryException
import org.springframework.messaging.simp.stomp.ConnectionLostException
import org.springframework.messaging.simp.stomp.StompCommand
import org.springframework.messaging.simp.stomp.StompHeaderAccessor
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Component
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler
import java.nio.charset.StandardCharsets

@Component
class CustomStompErrorHandler(
    private val objectMapper: ObjectMapper,
) : StompSubProtocolErrorHandler() {
    private val logger = KotlinLogging.logger {}

    override fun handleClientMessageProcessingError(
        clientMessage: Message<ByteArray?>?,
        ex: Throwable,
    ): Message<ByteArray?>? {
        if (ex is MessageDeliveryException) {
            val cause = ex.cause ?: return sendErrorMessage(ErrorResponse.from(ErrorCode.INTERNAL_SERVER_ERROR))
            if (cause is AccessDeniedException) {
                return sendErrorMessage(ErrorResponse.from(ErrorCode.ACCESS_DENIED))
            }

            if (cause is AuthorizationException) {
                return sendErrorMessage(ErrorResponse.from(ErrorCode.NOT_AUTHORIZED))
            }
        }
        if (ex is ConnectionLostException) {
            logger.warn("Client disconnected unexpectedly (499 Client Closed Request)")
            return null
        }
        return super.handleClientMessageProcessingError(clientMessage, ex)

        val errorMessage = "WebSocket Error: ${ex.message}"
        logger.error(errorMessage)
        return super.handleClientMessageProcessingError(clientMessage, ex)
    }

    private fun sendErrorMessage(errorResponse: ErrorResponse): Message<ByteArray?>? {
        val headers = StompHeaderAccessor.create(StompCommand.ERROR)
        headers.message = errorResponse.errorCode
        headers.setLeaveMutable(true)

        return try {
            val json = objectMapper.writeValueAsString(errorResponse)
            MessageBuilder.createMessage(json.toByteArray(StandardCharsets.UTF_8) as ByteArray?, headers.messageHeaders)
        } catch (e: Exception) {
            logger.error("Failed to convert ErrorResponse to JSON", e)
            MessageBuilder.createMessage(errorResponse.message.toByteArray(StandardCharsets.UTF_8) as ByteArray?, headers.messageHeaders)
        }
    }
}
