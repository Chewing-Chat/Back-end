package org.chewing.v1.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class OpenAiConfig(
    @Value("\${openai.api.key}")
    private val openAiKey: String,
    @Value("\${openai.api.url}")
    private val openAiUrl: String,
) {

    @Bean
    fun openAiWebClient(): WebClient = WebClient.builder()
        .baseUrl(openAiUrl)
        .filter(addAuthorizationHeader())
        .filter(logRequest())
        .build()

    private fun addAuthorizationHeader(): ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
        val modifiedRequest = ClientRequest.from(clientRequest)
            .header("Authorization", "Bearer $openAiKey")
            .build()
        Mono.just(modifiedRequest)
    }

    private fun logRequest(): ExchangeFilterFunction = ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
        Mono.just(clientRequest)
    }.andThen(
        ExchangeFilterFunction.ofResponseProcessor { clientResponse ->
            Mono.just(clientResponse)
        },
    )
}
