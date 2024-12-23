package org.chewing.v1.external

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import mu.KotlinLogging
import org.chewing.v1.client.FcmClient
import org.chewing.v1.dto.FcmMessageDto
import org.chewing.v1.model.notification.Notification
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class ExternalPushNotificationClientImpl(
    private val fcmClient: FcmClient,
) : ExternalPushNotificationClient {

    private val logger = KotlinLogging.logger {}

    @Throws(JsonParseException::class, JsonProcessingException::class)
    override suspend fun sendFcmNotification(notification: Notification) {
        try {
            fcmClient.execute(FcmMessageDto.from(notification))
        } catch (e: WebClientResponseException) {
            val responseBody = e.responseBodyAsString
            logger.warn { "Error response body: $responseBody" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send FCM notification" }
        }
    }
}
