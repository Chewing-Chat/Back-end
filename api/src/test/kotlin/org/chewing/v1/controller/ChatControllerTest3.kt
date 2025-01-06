package org.chewing.v1.controller

import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.implementation.auth.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
class ChatControllerTest3 : IntegrationTest() {

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @LocalServerPort
    private var port: Int = 0

    private val stompClient: WebSocketStompClient by lazy {
        WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = MappingJackson2MessageConverter()
        }
    }

    @Test
    fun `Authorization 헤더 없이 연결 시도 - 실패해야 함`() {
        val url = "ws://localhost:$port/ws-stomp"
        val webSocketHeaders = WebSocketHttpHeaders()
        val futureSession = stompClient.connectAsync(url, webSocketHeaders, object : StompSessionHandlerAdapter() {})

        val exception = assertThrows<Exception> {
            futureSession.get(1, TimeUnit.MINUTES)
        }
        println(exception)
    }
}
