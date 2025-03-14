package org.chewing.v1.external

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.response.ErrorResponse
import org.chewing.v1.util.security.JwtTokenUtil
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.*
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.nio.charset.StandardCharsets
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference

@ActiveProfiles("test")
class ExternalChatNotificationClientTest2 : IntegrationTest() {

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @LocalServerPort
    private var port: Int = 0

    private lateinit var session: StompSession
    private lateinit var errorLatch: CountDownLatch
    private val receivedErrors = AtomicReference<ErrorResponse>()
    private val objectMapper = jacksonObjectMapper().registerModule(KotlinModule.Builder().build())

    private val stompClient: WebSocketStompClient by lazy {

        val converter = MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
        }
        WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = converter
        }
    }

    @BeforeAll
    fun setup() {
        errorLatch = CountDownLatch(1)
    }

    @AfterAll
    fun tearDown() {
        if (::session.isInitialized) {
            session.disconnect()
        }
    }

    private fun connectStompSession(token: String): StompSession {
        val httpHeaders = WebSocketHttpHeaders()
        val stompHeaders = StompHeaders().apply {
            add("Authorization", "Bearer $token")
        }

        val url = "ws://localhost:$port/ws-stomp-pure"

        val futureSession = stompClient.connectAsync(
            url,
            httpHeaders,
            stompHeaders,
            object : StompSessionHandlerAdapter() {
                override fun handleException(session: StompSession, command: StompCommand?, headers: StompHeaders, payload: ByteArray, exception: Throwable) {
                    val jsonString = String(payload, StandardCharsets.UTF_8)
                    println("Received Error Payload: $jsonString") // 디버깅용 출력

                    // JSON 문자열을 ErrorResponse 객체로 변환
                    val errorResponse = objectMapper.readValue(jsonString, ErrorResponse::class.java)
                    receivedErrors.set(errorResponse)
                    errorLatch.countDown()
                }
            },
        )
        return futureSession.get(1, TimeUnit.MINUTES)
    }

    @Test
    fun `잘못된 토큰으로 WebSocket 연결 시 인증 오류가 발생해야 한다`() {
        // Given
        val invalidToken = "invalid_token"

        try {
            // When
            session = connectStompSession(invalidToken)
        } catch (e: Exception) {
            println("WebSocket 연결 중 예외 발생: ${e.message}")
        }

        // Then
        val errorOccurred = errorLatch.await(5, TimeUnit.SECONDS)

        val errorPayLoad = receivedErrors.get()

        assertThat(errorOccurred).isTrue()
        assertThat(errorPayLoad).isNotNull
        assertThat(errorPayLoad.errorCode == ErrorCode.NOT_AUTHORIZED.code)
    }
}
