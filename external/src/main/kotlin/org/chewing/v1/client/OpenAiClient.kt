package org.chewing.v1.client

import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.chewing.v1.dto.ChatGPTRequest
import org.chewing.v1.dto.ChatGPTResponse
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class OpenAiClient(
    @Qualifier("openAiWebClient") private val webClient: WebClient,
) {
    suspend fun execute(request: ChatGPTRequest): ChatGPTResponse? {
        return webClient.post()
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .bodyToMono(ChatGPTResponse::class.java)
            .awaitSingleOrNull()
    }
}
