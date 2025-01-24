package org.chewing.v1.config

import io.openvidu.java.client.OpenVidu
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class OpenViduConfig(
    @Value("\${openvidu.url}") private val openviduUrl: String,
    @Value("\${openvidu.secret}") private val openviduSecret: String,
) {

    @Bean
    fun openVidu(): OpenVidu {
        return OpenVidu(openviduUrl, openviduSecret)
    }
}
