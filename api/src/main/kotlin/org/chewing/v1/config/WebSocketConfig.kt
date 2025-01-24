package org.chewing.v1.config

import org.chewing.v1.util.handler.CustomHandshakeHandler
import org.chewing.v1.util.interceptor.StompHandshakeInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val stompCustomHandshakeHandler: CustomHandshakeHandler,
    private val stompHandshakeInterceptor: StompHandshakeInterceptor,
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        // WebSocket 연결 엔드포인트 등록
        registry.addEndpoint("/ws-stomp")
            .setAllowedOrigins("*") // 모든 출처 허용
            .addInterceptors(stompHandshakeInterceptor)
            .setHandshakeHandler(stompCustomHandshakeHandler)
            .withSockJS() // SockJS 지원 추가
        registry.addEndpoint("/ws-stomp")
            .setAllowedOrigins("*") // 모든 출처 허용
            .addInterceptors(stompHandshakeInterceptor)
            .setHandshakeHandler(stompCustomHandshakeHandler)
    }
}
