
package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.controller.ai.AiController
import org.chewing.v1.dto.request.chat.ClonePromptRequest
import org.chewing.v1.facade.AiFacade
import org.chewing.v1.model.chat.member.SenderType
import org.chewing.v1.model.chat.message.ChatAiMessage
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.chat.room.ChatRoomSequence
import org.chewing.v1.model.chat.room.ChatRoomType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.LocalDateTime

@ActiveProfiles("test")
class AiControllerTest : RestDocsTest() {

    private lateinit var aiFacade: AiFacade
    private lateinit var aiController: AiController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver

    @BeforeEach
    fun setUp() {
        aiFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        aiController = AiController(aiFacade)
        mockMvc = mockController(aiController, exceptionHandler, userArgumentResolver)

        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("AI 채팅방 생성")
    fun createAiChatRoom() {
        val chatRoomId = ChatRoomId.of("ai-room-001")
        every { aiFacade.produceAiChatRoom(any()) } returns chatRoomId

        given()
            .setupAuthenticatedJsonRequest()
            .post("/ai/chat/room")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .apply {
                body("status", equalTo(201))
                body("data.chatRoomId", equalTo(chatRoomId.id))
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("data.chatRoomId").description("AI 채팅방 ID")
                    ),
                    requestAccessTokenFields(),
                )
            )
    }

    @Test
    @DisplayName("일반 채팅방 복제 프롬프트 요청")
    fun cloneAiChatRoom() {
        val chatRoomId = ChatRoomId.of("ai-room-001")
        val request = ClonePromptRequest(
            prompt = "안녕",
            sourceChatRoomId = "chatroom-001",
            aiChatRoomId = "ai-room-001"
        )
        val aiMessage = ChatAiMessage.of(
            messageId = "msg-002",
            chatRoomId = chatRoomId,
            chatRoomType = ChatRoomType.AI,
            senderId = UserId.of("ai-user"),
            timestamp = LocalDateTime.now(),
            roomSequence = ChatRoomSequence.of(chatRoomId,2),
            text = "안녕하세요!",
            senderType = SenderType.AI
        )
        val userMessage = ChatAiMessage.of(
            messageId = "msg-001",
            chatRoomId = chatRoomId,
            chatRoomType = ChatRoomType.AI,
            senderId = UserId.of("testUserId"),
            timestamp = LocalDateTime.now(),
            roomSequence = ChatRoomSequence.of(chatRoomId, 1),
            text = request.prompt,
            senderType = SenderType.USER
        )
        every {
            aiFacade.cloneChatAsUserFromChatRoom(any(), any(), any(), any())
        } returns Pair(userMessage, aiMessage)

        given()
            .setupAuthenticatedJsonRequest()
            .body(request)
            .post("/ai/chat/clone")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                body("data.userMessage.messageId", equalTo(userMessage.messageId))
                body("data.userMessage.type", equalTo(userMessage.type.name.lowercase()))
                body("data.userMessage.senderId", equalTo(userMessage.senderId.id))
                body("data.userMessage.timestamp", equalTo(userMessage.timestamp.toString()))
                body("data.userMessage.seqNumber", equalTo(userMessage.roomSequence.sequence))
                body("data.userMessage.text", equalTo(userMessage.text))
                body("data.userMessage.senderType", equalTo(userMessage.senderType.name.lowercase()))

                body("data.aiMessage.messageId", equalTo(aiMessage.messageId))
                body("data.aiMessage.type", equalTo(aiMessage.type.name.lowercase()))
                body("data.aiMessage.senderId", equalTo(aiMessage.senderId.id))
                body("data.aiMessage.timestamp", equalTo(aiMessage.timestamp.toString()))
                body("data.aiMessage.seqNumber", equalTo(aiMessage.roomSequence.sequence))
                body("data.aiMessage.text", equalTo(aiMessage.text))
                body("data.aiMessage.senderType", equalTo(aiMessage.senderType.name.lowercase()))
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("prompt").description("사용자 입력 프롬프트"),
                        fieldWithPath("sourceChatRoomId").description("복제할 원본 채팅방 ID"),
                        fieldWithPath("aiChatRoomId").description("AI 채팅방 ID")
                    ),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("응답 상태"),
                        fieldWithPath("data.userMessage.messageId").description("AI 메시지 ID"),
                        fieldWithPath("data.userMessage.type").description("메시지 타입"),
                        fieldWithPath("data.userMessage.senderId").description("메시지 발신자 ID"),
                        fieldWithPath("data.userMessage.timestamp").description("메시지 타임스탬프"),
                        fieldWithPath("data.userMessage.seqNumber").description("채팅방 내 메시지 순서"),
                        fieldWithPath("data.userMessage.text").description("메시지 내용"),
                        fieldWithPath("data.userMessage.senderType").description("메시지 발신자 타입"),
                        fieldWithPath("data.aiMessage.messageId").description("AI 메시지 ID"),
                        fieldWithPath("data.aiMessage.type").description("메시지 타입"),
                        fieldWithPath("data.aiMessage.senderId").description("메시지 발신자 ID"),
                        fieldWithPath("data.aiMessage.timestamp").description("메시지 타임스탬프"),
                        fieldWithPath("data.aiMessage.seqNumber").description("채팅방 내 메시지 순서"),
                        fieldWithPath("data.aiMessage.text").description("메시지 내용"),
                        fieldWithPath("data.aiMessage.senderType").description("메시지 발신자 타입")
                    )
                )
            )
    }
}
