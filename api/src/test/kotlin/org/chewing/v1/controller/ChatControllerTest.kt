package org.chewing.v1.controller

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.verify
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.implementation.auth.JwtTokenProvider
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
class ChatControllerTest : IntegrationTest() {

    @Autowired
    private lateinit var jwtTokenProvider: JwtTokenProvider

    @LocalServerPort
    private var port: Int = 0

    private val userId = "testUserId"
    private lateinit var token: String
    private lateinit var session: StompSession

    private val stompClient: WebSocketStompClient by lazy {
        val objectMapper = jacksonObjectMapper().registerModule(KotlinModule())
        val converter = MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
        }
        WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = converter
        }
    }

    private fun connectStompSession(): StompSession {
        val headers = WebSocketHttpHeaders().apply {
            set("Authorization", "Bearer $token")
        }
        val url = "ws://localhost:$port/ws-stomp"

        // 재시도 메커니즘 구현
        val maxRetries = 5
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val futureSession = stompClient.connectAsync(
                    url,
                    headers,
                    object : StompSessionHandlerAdapter() {},
                )
                return futureSession.get(2, TimeUnit.MINUTES)
            } catch (e: Exception) {
                attempt++
                Thread.sleep(2000) // 2초 대기 후 재시도
                if (attempt == maxRetries) throw e
            }
        }
        throw IllegalStateException("Failed to connect to WebSocket after $maxRetries attempts")
    }

    @BeforeAll
    fun setup() {
        token = jwtTokenProvider.createAccessToken(userId)
        session = connectStompSession()
    }

    @Test
    fun `일반 메세지 전송`() {
        val latch = CountDownLatch(1)
        every { chatFacade.processCommon(any(), any(), any()) } answers {
            latch.countDown()
        }
        val chatDto = ChatRequest.Common("testRoomId", "testUserId")
        session.send("/app/chat/common", chatDto)
        latch.await(1, TimeUnit.MINUTES)
        verify { chatFacade.processCommon(chatDto.chatRoomId, userId, chatDto.message) }
    }

    @Test
    fun `읽기 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { chatFacade.processRead(any(), any()) } answers {
            latch.countDown()
        }

        val chatReadDto = ChatRequest.Read("testRoomId")
        session.send("/app/chat/read", chatReadDto)

        latch.await(1, TimeUnit.MINUTES)

        verify { chatFacade.processRead(chatReadDto.chatRoomId, userId) }
    }

    @Test
    fun `삭제 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { chatFacade.processDelete(any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatDeleteDto = ChatRequest.Delete("testRoomId", "testMessageId")
        session.send("/app/chat/delete", chatDeleteDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { chatFacade.processDelete(chatDeleteDto.chatRoomId, userId, chatDeleteDto.messageId) }
    }

    @Test
    fun `답장 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { chatFacade.processReply(any(), any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatReplyDto = ChatRequest.Reply("testRoomId", "testParentMessageId", "testMessage")
        session.send("/app/chat/reply", chatReplyDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { chatFacade.processReply(chatReplyDto.chatRoomId, userId, chatReplyDto.parentMessageId, chatReplyDto.message) }
    }

    @Test
    fun `폭탄 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { chatFacade.processBombing(any(), any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatBombMessage = ChatRequest.Bomb("testRoomId", "testMessage", "2024:10:22 13:45:30")
        session.send("/app/chat/bomb", chatBombMessage)

        latch.await(1, TimeUnit.MINUTES)
        verify { chatFacade.processBombing(chatBombMessage.chatRoomId, userId, chatBombMessage.message, chatBombMessage.toExpireAt()) }
    }
}
