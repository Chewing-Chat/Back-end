package org.chewing.v1.client

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.chewing.v1.dto.FcmMessageDto
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class FcmClient(
    @Qualifier("fcmWebClient") private val webClient: WebClient,
) {
    suspend fun execute(message: FcmMessageDto) {
        val mediaType = MediaType.parseMediaType("application/json; charset=UTF-8")
        webClient.post()
            .contentType(mediaType)
            .bodyValue(message)
            .retrieve()
            .bodyToMono(Void::class.java)
            .awaitSingleOrNull()
    }
}
