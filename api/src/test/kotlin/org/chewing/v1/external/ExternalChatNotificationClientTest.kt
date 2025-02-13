package org.chewing.v1.external

import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.assertj.core.api.Assertions.assertThat
import org.chewing.v1.TestDataFactory
import org.chewing.v1.config.IntegrationTest
import org.chewing.v1.dto.ChatMessageDto
import org.chewing.v1.util.security.JwtTokenUtil
import org.chewing.v1.implementation.session.SessionProvider
import org.chewing.v1.model.chat.message.ChatMessage
import org.chewing.v1.model.chat.message.MessageType
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.simp.stomp.StompFrameHandler
import org.springframework.messaging.simp.stomp.StompHeaders
import org.springframework.messaging.simp.stomp.StompSession
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter
import org.springframework.test.context.ActiveProfiles
import org.springframework.web.socket.WebSocketHttpHeaders
import org.springframework.web.socket.client.standard.StandardWebSocketClient
import org.springframework.web.socket.messaging.WebSocketStompClient
import java.lang.reflect.Type
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ActiveProfiles("test")
class ExternalChatNotificationClientTest : IntegrationTest() {

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @Autowired
    private lateinit var externalChatNotificationClient: ExternalChatNotificationClient

    @Autowired
    private lateinit var sessionProvider: SessionProvider

    @LocalServerPort
    private var port: Int = 0

    private lateinit var groupLatch: CountDownLatch
    private lateinit var directLatch: CountDownLatch
    private lateinit var session: StompSession

    private val userId = UserId.of("testUserId")
    private lateinit var token: String

    private val groupChatMessages = ConcurrentLinkedQueue<ChatMessageDto>()
    private val directChatMessages = ConcurrentLinkedQueue<ChatMessageDto>()

    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    private val stompClient: WebSocketStompClient by lazy {
        val objectMapper = jacksonObjectMapper().registerModule(KotlinModule.Builder().build())
        val converter = MappingJackson2MessageConverter().apply {
            this.objectMapper = objectMapper
        }
        WebSocketStompClient(StandardWebSocketClient()).apply {
            messageConverter = converter
        }
    }

    @BeforeAll
    fun setup() {
        // 메시지 개수에 따라 CountDownLatch 크기 설정
        groupLatch = CountDownLatch(7)
        directLatch = CountDownLatch(5)

        token = jwtTokenUtil.createAccessToken(userId)
        session = connectStompSession()

        // 그룹 채팅 메시지 구독
        session.subscribe("/user/queue/chat/group", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = ChatMessageDto::class.java
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                val message = payload as ChatMessageDto
                groupChatMessages.add(message)
                groupLatch.countDown()
            }
        })

        // 개인 채팅 메시지 구독
        session.subscribe("/user/queue/chat/direct", object : StompFrameHandler {
            override fun getPayloadType(headers: StompHeaders): Type = ChatMessageDto::class.java
            override fun handleFrame(headers: StompHeaders, payload: Any?) {
                val message = payload as ChatMessageDto
                directChatMessages.add(message)
                directLatch.countDown()
            }
        })
    }

    @AfterAll
    fun tearDown() {
        if (::session.isInitialized) {
            session.disconnect()
        }
    }

    private fun connectStompSession(): StompSession {
        val headers = WebSocketHttpHeaders().apply {
            set("Authorization", "Bearer $token")
        }
        val url = "ws://localhost:$port/ws-stomp"

        val futureSession = stompClient.connectAsync(url, headers, object : StompSessionHandlerAdapter() {})
        return futureSession.get(1, TimeUnit.MINUTES)
    }

    /**
     * 메시지를 전송하고 잠시 대기 후 latch 대기
     */
    private fun sendMessagesAndAwaitLatch(
        latch: CountDownLatch,
        waitSeconds: Long = 10L,
        vararg messages: Pair<ChatMessage, UserId>
    ) {
        messages.forEach { (chatData, sender) ->
            externalChatNotificationClient.sendMessage(chatData, sender)
        }
        latch.await(waitSeconds, TimeUnit.SECONDS)
    }

    @Test
    fun `개인 채팅 메시지 전송`() {
        val testChatRoomId1 = ChatRoomId.of("testChatRoomId1")
        val testChatRoomId3 = ChatRoomId.of("testChatRoomId3")
        val testChatRoomId4 = ChatRoomId.of("testChatRoomId4")
        val testChatRoomId5 = ChatRoomId.of("testChatRoomId5")
        val testChatRoomId6 = ChatRoomId.of("testChatRoomId6")

        val normalMessage = TestDataFactory.createNormalMessage("testMessageId1", testChatRoomId1, ChatRoomType.DIRECT)
        val fileMessage = TestDataFactory.createFileMessage("testMessageId3", testChatRoomId3, ChatRoomType.DIRECT)
        val deleteMessage = TestDataFactory.createDeleteMessage("testMessageId4", testChatRoomId4, ChatRoomType.DIRECT)
        val replyMessage = TestDataFactory.createReplyMessage("testMessageId5", testChatRoomId5, ChatRoomType.DIRECT)
        val errorMessage = TestDataFactory.createErrorMessage(testChatRoomId6, ChatRoomType.DIRECT)

        // 메시지 전송 + Latch 대기
        sendMessagesAndAwaitLatch(
            directLatch,
            messages = arrayOf(
                normalMessage to userId,
                fileMessage to userId,
                deleteMessage to userId,
                replyMessage to userId,
                errorMessage to userId
            )
        )

        // 검증
        assertThat(directChatMessages.size).isEqualTo(5)
        directChatMessages.forEach { dto ->
            when (dto) {
                is ChatMessageDto.Delete -> {
                    assertThat(dto.targetMessageId).isEqualTo("testMessageId4")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId4.id)
                    assertThat(dto.senderId).isEqualTo(deleteMessage.senderId.id)
                    assertThat(dto.type).isEqualTo(MessageType.DELETE.name.lowercase())
                    assertThat(dto.timestamp).isEqualTo(deleteMessage.timestamp.format(dateFormat))
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.DIRECT.name.lowercase())
                }
                is ChatMessageDto.File -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId3")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId3.id)
                    assertThat(dto.senderId).isEqualTo(fileMessage.senderId.id)
                    assertThat(dto.type).isEqualTo(MessageType.FILE.name.lowercase())
                    assertThat(dto.timestamp).isEqualTo(fileMessage.timestamp.format(dateFormat))
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.DIRECT.name.lowercase())
                    dto.files.forEachIndexed { index, mediaDto ->
                        assertThat(mediaDto.fileUrl).isEqualTo(fileMessage.medias[index].url)
                        assertThat(mediaDto.fileType).isEqualTo(fileMessage.medias[index].type.value())
                        assertThat(mediaDto.index).isEqualTo(fileMessage.medias[index].index)
                    }
                }
                is ChatMessageDto.Normal -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId1")
                    assertThat(dto.senderId).isEqualTo(normalMessage.senderId.id)
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId1.id)
                    assertThat(dto.type).isEqualTo(MessageType.NORMAL.name.lowercase())
                    assertThat(dto.timestamp).isEqualTo(normalMessage.timestamp.format(dateFormat))
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.DIRECT.name.lowercase())
                }
                is ChatMessageDto.Reply -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId5")
                    assertThat(dto.senderId).isEqualTo(replyMessage.senderId.id)
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId5.id)
                    assertThat(dto.type).isEqualTo(MessageType.REPLY.name.lowercase())
                    assertThat(dto.parentMessageId).isEqualTo(replyMessage.parentMessageId)
                    assertThat(dto.parentSeqNumber).isEqualTo(replyMessage.parentSeqNumber)
                    assertThat(dto.parentMessageText).isEqualTo(replyMessage.parentMessageText)
                    assertThat(dto.timestamp).isEqualTo(replyMessage.timestamp.format(dateFormat))
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.DIRECT.name.lowercase())
                }
                is ChatMessageDto.Error -> {
                    assertThat(dto.senderId).isEqualTo(errorMessage.senderId.id)
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId6.id)
                    assertThat(dto.errorCode).isEqualTo(errorMessage.errorCode.code)
                    assertThat(dto.errorMessage).isEqualTo(errorMessage.errorCode.message)
                    assertThat(dto.type).isEqualTo(MessageType.ERROR.name.lowercase())
                    assertThat(dto.timestamp).isEqualTo(errorMessage.timestamp.format(dateFormat))
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.DIRECT.name.lowercase())
                }
                is ChatMessageDto.Read -> {}
                is ChatMessageDto.Invite -> {}
                is ChatMessageDto.Leave -> {}
            }
        }
    }

    @Test
    fun `그룹 채팅 메시지 전송`() {
        val testChatRoomId1 = ChatRoomId.of("testChatRoomId1")
        val testChatRoomId2 = ChatRoomId.of("testChatRoomId2")
        val testChatRoomId3 = ChatRoomId.of("testChatRoomId3")
        val testChatRoomId4 = ChatRoomId.of("testChatRoomId4")
        val testChatRoomId5 = ChatRoomId.of("testChatRoomId5")
        val testChatRoomId7 = ChatRoomId.of("testChatRoomId7")
        val testChatRoomId8 = ChatRoomId.of("testChatRoomId8")

        val normalMessage = TestDataFactory.createNormalMessage("testMessageId1", testChatRoomId1, ChatRoomType.GROUP)
        val inviteMessage = TestDataFactory.createInviteMessage("testMessageId2", testChatRoomId2, ChatRoomType.GROUP)
        val fileMessage = TestDataFactory.createFileMessage("testMessageId3", testChatRoomId3, ChatRoomType.GROUP)
        val deleteMessage = TestDataFactory.createDeleteMessage("testMessageId4", testChatRoomId4, ChatRoomType.GROUP)
        val replyMessage = TestDataFactory.createReplyMessage("testMessageId5", testChatRoomId5, ChatRoomType.GROUP)
        val leaveMessage = TestDataFactory.createLeaveMessage("testMessageId7", testChatRoomId7, ChatRoomType.GROUP)
        val errorMessage = TestDataFactory.createErrorMessage(testChatRoomId8, ChatRoomType.GROUP)

        // 메시지 전송 + Latch 대기
        sendMessagesAndAwaitLatch(
            groupLatch,
            messages = arrayOf(
                normalMessage to userId,
                inviteMessage to userId,
                fileMessage to userId,
                deleteMessage to userId,
                replyMessage to userId,
                leaveMessage to userId,
                errorMessage to userId
            )
        )

        assertThat(groupChatMessages.size).isEqualTo(7)

        groupChatMessages.forEach { dto ->
            when (dto) {
                is ChatMessageDto.Delete -> {
                    assertThat(dto.targetMessageId).isEqualTo("testMessageId4")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId4.id)
                    assertThat(dto.senderId).isEqualTo(deleteMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(deleteMessage.number.sequenceNumber)
                    assertThat(dto.timestamp).isEqualTo(deleteMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.DELETE.name.lowercase())
                }
                is ChatMessageDto.File -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId3")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId3.id)
                    assertThat(dto.senderId).isEqualTo(fileMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(fileMessage.number.sequenceNumber)
                    assertThat(dto.timestamp).isEqualTo(fileMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.FILE.name.lowercase())
                    dto.files.forEachIndexed { index, mediaDto ->
                        assertThat(mediaDto.fileUrl).isEqualTo(fileMessage.medias[index].url)
                        assertThat(mediaDto.fileType).isEqualTo(fileMessage.medias[index].type.value())
                        assertThat(mediaDto.index).isEqualTo(fileMessage.medias[index].index)
                    }
                }
                is ChatMessageDto.Invite -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId2")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId2.id)
                    assertThat(dto.senderId).isEqualTo(inviteMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(inviteMessage.number.sequenceNumber)
                    assertThat(dto.timestamp).isEqualTo(inviteMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.INVITE.name.lowercase())
                }
                is ChatMessageDto.Leave -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId7")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId7.id)
                    assertThat(dto.senderId).isEqualTo(leaveMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(leaveMessage.number.sequenceNumber)
                    assertThat(dto.timestamp).isEqualTo(leaveMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.LEAVE.name.lowercase())
                }
                is ChatMessageDto.Normal -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId1")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId1.id)
                    assertThat(dto.senderId).isEqualTo(normalMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(normalMessage.number.sequenceNumber)
                    assertThat(dto.timestamp).isEqualTo(normalMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.NORMAL.name.lowercase())
                }
                is ChatMessageDto.Reply -> {
                    assertThat(dto.messageId).isEqualTo("testMessageId5")
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId5.id)
                    assertThat(dto.senderId).isEqualTo(replyMessage.senderId.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.seqNumber).isEqualTo(replyMessage.number.sequenceNumber)
                    assertThat(dto.parentMessageId).isEqualTo(replyMessage.parentMessageId)
                    assertThat(dto.parentSeqNumber).isEqualTo(replyMessage.parentSeqNumber)
                    assertThat(dto.parentMessageText).isEqualTo(replyMessage.parentMessageText)
                    assertThat(dto.timestamp).isEqualTo(replyMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.REPLY.name.lowercase())
                }
                is ChatMessageDto.Error -> {
                    assertThat(dto.senderId).isEqualTo(errorMessage.senderId.id)
                    assertThat(dto.chatRoomId).isEqualTo(testChatRoomId8.id)
                    assertThat(dto.chatRoomType).isEqualTo(ChatRoomType.GROUP.name.lowercase())
                    assertThat(dto.errorCode).isEqualTo(errorMessage.errorCode.code)
                    assertThat(dto.errorMessage).isEqualTo(errorMessage.errorCode.message)
                    assertThat(dto.timestamp).isEqualTo(errorMessage.timestamp.format(dateFormat))
                    assertThat(dto.type).isEqualTo(MessageType.ERROR.name.lowercase())
                }
                is ChatMessageDto.Read -> {}
            }
        }
    }
}
