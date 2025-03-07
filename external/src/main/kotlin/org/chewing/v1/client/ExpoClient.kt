package org.chewing.v1.client

import kotlinx.coroutines.reactor.awaitSingleOrNull
import mu.KotlinLogging
import org.chewing.v1.dto.ExpoMessageDto
import org.chewing.v1.dto.ExpoPushResponse
import org.chewing.v1.dto.ExpoPushTicket
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class ExpoClient(
    @Qualifier("expoWebClient") private val webClient: WebClient,
) {
    private val logger = KotlinLogging.logger {}
    private val chunkSize = 100

    suspend fun sendPushNotification(messages: List<ExpoMessageDto>) {
        val allTickets = mutableListOf<ExpoPushTicket>()

        messages.chunked(chunkSize).forEach { chunk ->
            val response = webClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(chunk)
                .retrieve()
                .bodyToMono(ExpoPushResponse::class.java)
                .awaitSingleOrNull()

            response?.data?.let { tickets ->
                allTickets.addAll(tickets)
            }
        }

        // 성공 티켓과 오류 티켓을 분류하여 로깅
        val okTickets = allTickets.filter { it.status == "ok" }
        val errorTickets = allTickets.filter { it.status != "ok" }

        val okTicketMessagesString = okTickets.joinToString(",") {
            "Ticket: ${it.id}"
        }
        logger.info("Received OK ticket for ${okTickets.size} messages: $okTicketMessagesString")

        if (errorTickets.isNotEmpty()) {
            val errorTicketMessagesString = errorTickets.joinToString(",") {
                "Ticket: ${it.id}, Error: ${it.message}"
            }
            logger.error("Received ERROR ticket for ${errorTickets.size} messages: $errorTicketMessagesString")
        }
    }
}
