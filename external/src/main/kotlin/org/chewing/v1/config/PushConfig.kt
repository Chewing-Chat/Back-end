package org.chewing.v1.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class PushConfig() {
    private val expoPushUrl = "https://exp.host/--/api/v2/push/send"

    @Bean
    fun expoWebClient(): WebClient = WebClient.builder()
        .baseUrl(expoPushUrl)
        .build()
}
