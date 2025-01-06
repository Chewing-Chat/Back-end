package org.chewing.v1.config

import com.google.auth.oauth2.GoogleCredentials
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Configuration
class FcmConfig(
    @Value("\${fcm.api.url}")
    private val fcmApiUrl: String,

    @Value("\${fcm.api.resource}")
    private val resource: ClassPathResource,
) {

    @Bean
    fun fcmWebClient(): WebClient = WebClient.builder()
        .baseUrl(fcmApiUrl)
        .filter(addAuthorizationHeader())
        .build()

    private fun addAuthorizationHeader(): ExchangeFilterFunction =
        ExchangeFilterFunction.ofRequestProcessor { clientRequest ->
            val accessToken = getAccessToken()
            val modifiedRequest = ClientRequest.from(clientRequest)
                .header("Authorization", "Bearer $accessToken")
                .build()
            Mono.just(modifiedRequest)
        }

    fun getAccessToken(): String {
        val googleCredentials = GoogleCredentials
            .fromStream(resource.inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
        googleCredentials.refreshIfExpired()
        return googleCredentials.accessToken.tokenValue
    }
}
