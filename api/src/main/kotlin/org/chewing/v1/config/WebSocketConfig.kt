package org.chewing.v1.config

import org.chewing.v1.util.handler.CustomHandshakeHandler
import org.chewing.v1.util.handler.CustomStompErrorHandler
import org.chewing.v1.util.interceptor.StompChannelInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.simp.config.ChannelRegistration
import org.springframework.messaging.simp.config.MessageBrokerRegistry
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker
import org.springframework.web.socket.config.annotation.StompEndpointRegistry
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer

@Configuration
@EnableWebSocketMessageBroker
class WebSocketConfig(
    private val stompCustomHandshakeHandler: CustomHandshakeHandler,
    private val stompChannelInterceptor: StompChannelInterceptor,
    private val customStompErrorHandler: CustomStompErrorHandler,
) : WebSocketMessageBrokerConfigurer {
    override fun configureMessageBroker(config: MessageBrokerRegistry) {
        config.enableSimpleBroker("/topic", "/queue")
        config.setApplicationDestinationPrefixes("/app")
        config.setUserDestinationPrefix("/user")
    }

    override fun registerStompEndpoints(registry: StompEndpointRegistry) {
        registry.addEndpoint("/ws-stomp")
            .setAllowedOriginPatterns("*")
            .setHandshakeHandler(stompCustomHandshakeHandler)
            .withSockJS()
        registry.addEndpoint("/ws-stomp-pure")
            .setAllowedOriginPatterns("*")
            .setHandshakeHandler(stompCustomHandshakeHandler)
        registry.setErrorHandler(customStompErrorHandler)
    }

    override fun configureClientInboundChannel(registration: ChannelRegistration) {
        registration.interceptors(stompChannelInterceptor)
    }
}
