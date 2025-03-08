package org.chewing.v1.external

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonProcessingException
import mu.KotlinLogging
import org.chewing.v1.client.ExpoClient
import org.chewing.v1.dto.ExpoMessageDto
import org.chewing.v1.model.notification.Notification
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClientResponseException

@Component
class ExternalPushNotificationClientImpl(
    private val expoClient: ExpoClient,
) : ExternalPushNotificationClient {

    private val logger = KotlinLogging.logger {}

    @Throws(JsonParseException::class, JsonProcessingException::class)
    override suspend fun sendPushNotifications(notificationList: List<Notification>) {
        try {
            val messageDtos = notificationList.map { ExpoMessageDto.from(it) }
            expoClient.sendPushNotification(messageDtos)
        } catch (e: WebClientResponseException) {
            val responseBody = e.responseBodyAsString
            logger.warn { "Error response body: $responseBody" }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send Expo notification" }
        }
    }
}
