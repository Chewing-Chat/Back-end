package org.chewing.v1.controller

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.mockk.every
import io.mockk.verify
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.dto.request.chat.ChatRequest
import org.chewing.v1.util.security.JwtTokenUtil
import org.chewing.v1.model.user.UserId
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompHeaders
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
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @LocalServerPort
    private var port: Int = 0

    private val userId = UserId.of("testUserId")
    private lateinit var token: String
    private lateinit var session: StompSession

    private val stompClient: WebSocketStompClient by lazy {
        val objectMapper = jacksonObjectMapper().registerModule(
            KotlinModule.Builder().build(),
        )
        val converter = MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
        }
        WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = converter
        }
    }

    private fun connectStompSession(): StompSession {
        val httpHeaders = WebSocketHttpHeaders()
        val stompHeaders = StompHeaders().apply {
            add("Authorization", "Bearer $token")
        }
        val url = "ws://localhost:$port/ws-stomp"

        // 재시도 메커니즘 구현
        val maxRetries = 5
        var attempt = 0
        while (attempt < maxRetries) {
            try {
                val futureSession = stompClient.connectAsync(
                    url,
                    httpHeaders,
                    stompHeaders,
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
        token = jwtTokenUtil.createAccessToken(userId)
        session = connectStompSession()
    }

    @Test
    fun `개인 채팅방 일반 메세지 전송`() {
        val latch = CountDownLatch(1)
        every { directChatFacade.processDirectChatCommon(any(), any(), any()) } answers {
            latch.countDown()
        }
        val chatDto = ChatRequest.Common("testRoomId", "testMessage")
        session.send("/app/chat/direct/common", chatDto)
        latch.await(1, TimeUnit.MINUTES)
        verify { directChatFacade.processDirectChatCommon(chatDto.toChatRoomId(), any(), chatDto.message) }
    }

    @Test
    fun `개인 채팅방 읽기 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { directChatFacade.processDirectChatRead(any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatReadDto = ChatRequest.Read("testRoomId", 0)
        session.send("/app/chat/direct/read", chatReadDto)

        latch.await(1, TimeUnit.MINUTES)

        verify { directChatFacade.processDirectChatRead(chatReadDto.toChatRoomId(), any(), any()) }
    }

    @Test
    fun `개인 채팅방 삭제 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { directChatFacade.processDirectChatDelete(any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatDeleteDto = ChatRequest.Delete("testRoomId", "testMessageId")
        session.send("/app/chat/direct/delete", chatDeleteDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { directChatFacade.processDirectChatDelete(chatDeleteDto.toChatRoomId(), userId, chatDeleteDto.messageId) }
    }

    @Test
    fun `개인 채팅방 답장 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { directChatFacade.processDirectChatReply(any(), any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatReplyDto = ChatRequest.Reply("testRoomId", "testParentMessageId", "testMessage")
        session.send("/app/chat/direct/reply", chatReplyDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { directChatFacade.processDirectChatReply(chatReplyDto.toChatRoomId(), userId, chatReplyDto.parentMessageId, chatReplyDto.message) }
    }

    @Test
    fun `그룹 채팅방 일반 메세지 전송`() {
        val latch = CountDownLatch(1)
        every { groupChatFacade.processGroupChatCommon(any(), any(), any()) } answers {
            latch.countDown()
        }
        val chatDto = ChatRequest.Common("testRoomId", "testMessage")
        session.send("/app/chat/group/common", chatDto)
        latch.await(1, TimeUnit.MINUTES)
        verify { groupChatFacade.processGroupChatCommon(chatDto.toChatRoomId(), any(), chatDto.message) }
    }

    @Test
    fun `그룹 채팅방 읽기 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { groupChatFacade.processGroupChatRead(any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatReadDto = ChatRequest.Read("testRoomId", 0)
        session.send("/app/chat/group/read", chatReadDto)

        latch.await(1, TimeUnit.MINUTES)

        verify { groupChatFacade.processGroupChatRead(chatReadDto.toChatRoomId(), any(), any()) }
    }

    @Test
    fun `그룹 채팅방 삭제 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { groupChatFacade.processGroupChatDelete(any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatDeleteDto = ChatRequest.Delete("testRoomId", "testMessageId")
        session.send("/app/chat/group/delete", chatDeleteDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { groupChatFacade.processGroupChatDelete(chatDeleteDto.toChatRoomId(), userId, chatDeleteDto.messageId) }
    }

    @Test
    fun `그룹 채팅방 답장 메시지 전송`() {
        val latch = CountDownLatch(1)
        every { groupChatFacade.processGroupChatReply(any(), any(), any(), any()) } answers {
            latch.countDown()
        }

        val chatReplyDto = ChatRequest.Reply("testRoomId", "testParentMessageId", "testMessage")
        session.send("/app/chat/group/reply", chatReplyDto)

        latch.await(1, TimeUnit.MINUTES)
        verify { groupChatFacade.processGroupChatReply(chatReplyDto.toChatRoomId(), userId, chatReplyDto.parentMessageId, chatReplyDto.message) }
    }
}
