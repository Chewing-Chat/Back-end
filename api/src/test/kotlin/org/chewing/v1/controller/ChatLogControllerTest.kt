package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.chat.ChatLogController
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.chat.log.ChatFileLog
import org.chewing.v1.model.chat.log.ChatInviteLog
import org.chewing.v1.model.chat.log.ChatLeaveLog
import org.chewing.v1.model.chat.log.ChatNormalLog
import org.chewing.v1.model.chat.log.ChatReplyLog
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class ChatLogControllerTest : RestDocsTest() {
    private lateinit var directChatFacade: DirectChatFacade
    private lateinit var groupChatFacade: GroupChatFacade
    private lateinit var chatLogController: ChatLogController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver

    @BeforeEach
    fun setUp() {
        directChatFacade = mockk()
        groupChatFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        chatLogController = ChatLogController(groupChatFacade, directChatFacade)
        mockMvc = mockController(chatLogController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun getDirectChatLogs() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatFileLog = TestDataFactory.createFileLog(chatRoomId)
        val chatReplyLog = TestDataFactory.createReplyLog(chatRoomId)
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)
        val sequenceNumber = 100

        val chatLogs = listOf(
            chatFileLog,
            chatReplyLog,
            chatNormalLog,
        )
        every { directChatFacade.processDirectChatLogs(any(), any(), any()) } returns chatLogs
        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("sequenceNumber", sequenceNumber)
            .get("/api/chatRoom/{chatRoomId}/direct/log", chatRoomId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                chatLogs.forEachIndexed {
                        index, chatLog ->
                    val prefix = "data.chatLogs[$index]"
                    when (chatLog) {
                        is ChatFileLog -> {
                            body("$prefix.messageId", equalTo(chatFileLog.messageId))
                            body("$prefix.type", equalTo(chatFileLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatFileLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatFileLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatFileLog.roomSequence.sequence))
                            chatFileLog.medias.forEachIndexed {
                                    mediaIndex, media ->
                                val mediaPrefix = "$prefix.files[$mediaIndex]"
                                body("$mediaPrefix.fileType", equalTo(media.type.value()))
                                body("$mediaPrefix.fileUrl", equalTo(media.url))
                                body("$mediaPrefix.index", equalTo(media.index))
                            }
                        }
                        is ChatNormalLog -> {
                            body("$prefix.messageId", equalTo(chatNormalLog.messageId))
                            body("$prefix.type", equalTo(chatNormalLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatNormalLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatNormalLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatNormalLog.roomSequence.sequence))
                            body("$prefix.text", equalTo(chatNormalLog.text))
                        }
                        is ChatReplyLog -> {
                            body("$prefix.messageId", equalTo(chatReplyLog.messageId))
                            body("$prefix.type", equalTo(chatReplyLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatReplyLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatReplyLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatReplyLog.roomSequence.sequence))
                            body("$prefix.parentMessageId", equalTo(chatReplyLog.parentMessageId))
                            body("$prefix.parentSeqNumber", equalTo(chatReplyLog.parentSeqNumber))
                            body("$prefix.parentMessageText", equalTo(chatReplyLog.parentMessageText))
                            body("$prefix.parentMessageType", equalTo(chatReplyLog.parentMessageType.toString().lowercase()))
                            body("$prefix.text", equalTo(chatReplyLog.text))
                        }
                        else -> {}
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    queryParameters(
                        parameterWithName("sequenceNumber").description("시퀀스 번호"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),

                        // 공통 필드
                        fieldWithPath("data.chatLogs[].messageId").description("메시지 ID (모든 타입 공통)"),
                        fieldWithPath("data.chatLogs[].type").description("메시지 타입: normal, file, reply, invite, leave"),
                        fieldWithPath("data.chatLogs[].senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.chatLogs[].timestamp").description("보낸 시간(yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("data.chatLogs[].seqNumber").description("시퀀스 번호"),

                        // normal/ reply 등에서 쓰이는 text
                        fieldWithPath("data.chatLogs[].text")
                            .optional()
                            .description("메시지 내용 (normal, reply 타입에서 사용)"),

                        // reply 전용
                        fieldWithPath("data.chatLogs[].parentMessageId")
                            .optional()
                            .description("부모 메시지 ID (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentSeqNumber")
                            .optional()
                            .description("부모 메시지 시퀀스 번호 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageText")
                            .optional()
                            .description("부모 메시지 내용 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageType")
                            .optional()
                            .description("부모 메시지 타입 (reply 타입에서만 사용)"),

                        // file 전용
                        fieldWithPath("data.chatLogs[].files[].fileType")
                            .optional()
                            .description("파일 타입 (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].fileUrl")
                            .optional()
                            .description("파일 URL (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].index")
                            .optional()
                            .description("파일 인덱스 (file 메시지에서만 사용)"),
                    ),
                ),
            )
    }

    @Test
    fun getGroupChatLogs() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatFileLog = TestDataFactory.createFileLog(chatRoomId)
        val chatReplyLog = TestDataFactory.createReplyLog(chatRoomId)
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)
        val chatInviteLog = TestDataFactory.createInviteLog(chatRoomId)
        val chatLeaveLog = TestDataFactory.createLeaveLog(chatRoomId)
        val sequenceNumber = 100

        val chatLogs = listOf(
            chatFileLog,
            chatReplyLog,
            chatNormalLog,
            chatInviteLog,
            chatLeaveLog,
        )
        every { groupChatFacade.processGroupChatLogs(any(), any(), any()) } returns chatLogs
        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("sequenceNumber", sequenceNumber)
            .get("/api/chatRoom/{chatRoomId}/group/log", chatRoomId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                chatLogs.forEachIndexed { index, chatLog ->
                    val prefix = "data.chatLogs[$index]"
                    when (chatLog) {
                        is ChatFileLog -> {
                            body("$prefix.messageId", equalTo(chatFileLog.messageId))
                            body("$prefix.type", equalTo(chatFileLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatFileLog.senderId.id))
                            body(
                                "$prefix.timestamp",
                                equalTo(chatFileLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            )
                            body("$prefix.seqNumber", equalTo(chatFileLog.roomSequence.sequence))
                            chatFileLog.medias.forEachIndexed { mediaIndex, media ->
                                val mediaPrefix = "$prefix.files[$mediaIndex]"
                                body("$mediaPrefix.fileType", equalTo(media.type.value()))
                                body("$mediaPrefix.fileUrl", equalTo(media.url))
                                body("$mediaPrefix.index", equalTo(media.index))
                            }
                        }

                        is ChatNormalLog -> {
                            body("$prefix.messageId", equalTo(chatNormalLog.messageId))
                            body("$prefix.type", equalTo(chatNormalLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatNormalLog.senderId.id))
                            body(
                                "$prefix.timestamp",
                                equalTo(chatNormalLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            )
                            body("$prefix.seqNumber", equalTo(chatNormalLog.roomSequence.sequence))
                            body("$prefix.text", equalTo(chatNormalLog.text))
                        }

                        is ChatReplyLog -> {
                            body("$prefix.messageId", equalTo(chatReplyLog.messageId))
                            body("$prefix.type", equalTo(chatReplyLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatReplyLog.senderId.id))
                            body(
                                "$prefix.timestamp",
                                equalTo(chatReplyLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            )
                            body("$prefix.seqNumber", equalTo(chatReplyLog.roomSequence.sequence))
                            body("$prefix.parentMessageId", equalTo(chatReplyLog.parentMessageId))
                            body("$prefix.parentSeqNumber", equalTo(chatReplyLog.parentSeqNumber))
                            body("$prefix.parentMessageText", equalTo(chatReplyLog.parentMessageText))
                            body(
                                "$prefix.parentMessageType",
                                equalTo(chatReplyLog.parentMessageType.toString().lowercase()),
                            )
                            body("$prefix.text", equalTo(chatReplyLog.text))
                        }

                        is ChatInviteLog -> {
                            body("$prefix.messageId", equalTo(chatInviteLog.messageId))
                            body("$prefix.type", equalTo(chatInviteLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatInviteLog.senderId.id))
                            body(
                                "$prefix.timestamp",
                                equalTo(chatInviteLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            )
                            body("$prefix.seqNumber", equalTo(chatInviteLog.roomSequence.sequence))
                            chatInviteLog.targetUserIds.forEachIndexed { userIdIndex, userId ->
                                val userIdPrefix = "$prefix.targetUserIds[$userIdIndex]"
                                body(userIdPrefix, equalTo(userId.id))
                            }
                        }

                        is ChatLeaveLog -> {
                            body("$prefix.messageId", equalTo(chatLeaveLog.messageId))
                            body("$prefix.type", equalTo(chatLeaveLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatLeaveLog.senderId.id))
                            body(
                                "$prefix.timestamp",
                                equalTo(chatLeaveLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                            )
                            body("$prefix.seqNumber", equalTo(chatLeaveLog.roomSequence.sequence))
                        }
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    queryParameters(
                        parameterWithName("sequenceNumber").description("시퀀스 번호"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),

                        // 공통 필드
                        fieldWithPath("data.chatLogs[].messageId").description("메시지 ID (모든 타입 공통)"),
                        fieldWithPath("data.chatLogs[].type").description("메시지 타입: normal, file, reply, invite, leave"),
                        fieldWithPath("data.chatLogs[].senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.chatLogs[].timestamp").description("보낸 시간(yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("data.chatLogs[].seqNumber").description("시퀀스 번호"),

                        // normal/ reply 등에서 쓰이는 text
                        fieldWithPath("data.chatLogs[].text")
                            .optional()
                            .description("메시지 내용 (normal, reply 타입에서 사용)"),

                        // reply 전용
                        fieldWithPath("data.chatLogs[].parentMessageId")
                            .optional()
                            .description("부모 메시지 ID (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentSeqNumber")
                            .optional()
                            .description("부모 메시지 시퀀스 번호 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageText")
                            .optional()
                            .description("부모 메시지 내용 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageType")
                            .optional()
                            .description("부모 메시지 타입 (reply 타입에서만 사용)"),

                        // file 전용
                        fieldWithPath("data.chatLogs[].files[].fileType")
                            .optional()
                            .description("파일 타입 (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].fileUrl")
                            .optional()
                            .description("파일 URL (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].index")
                            .optional()
                            .description("파일 인덱스 (file 메시지에서만 사용)"),

                        // invite 전용
                        fieldWithPath("data.chatLogs[].targetUserIds[]")
                            .optional()
                            .description("초대된 사용자 ID 리스트 (invite 메시지에서만 사용)"),
                    ),
                ),
            )
    }

    @Test
    fun searchGroupChatLogs() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatFileLog = TestDataFactory.createFileLog(chatRoomId)
        val chatReplyLog = TestDataFactory.createReplyLog(chatRoomId)
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)

        val chatLogs = listOf(
            chatFileLog,
            chatReplyLog,
            chatNormalLog,
        )
        every { groupChatFacade.searchChatLog(any(), any(), any()) } returns chatLogs
        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("keyword", "keyword")
            .get("/api/chatRoom/{chatRoomId}/group/log/search", chatRoomId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                chatLogs.forEachIndexed {
                        index, chatLog ->
                    val prefix = "data.chatLogs[$index]"
                    when (chatLog) {
                        is ChatFileLog -> {
                            body("$prefix.messageId", equalTo(chatFileLog.messageId))
                            body("$prefix.type", equalTo(chatFileLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatFileLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatFileLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatFileLog.roomSequence.sequence))
                            chatFileLog.medias.forEachIndexed {
                                    mediaIndex, media ->
                                val mediaPrefix = "$prefix.files[$mediaIndex]"
                                body("$mediaPrefix.fileType", equalTo(media.type.value()))
                                body("$mediaPrefix.fileUrl", equalTo(media.url))
                                body("$mediaPrefix.index", equalTo(media.index))
                            }
                        }
                        is ChatNormalLog -> {
                            body("$prefix.messageId", equalTo(chatNormalLog.messageId))
                            body("$prefix.type", equalTo(chatNormalLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatNormalLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatNormalLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatNormalLog.roomSequence.sequence))
                            body("$prefix.text", equalTo(chatNormalLog.text))
                        }
                        is ChatReplyLog -> {
                            body("$prefix.messageId", equalTo(chatReplyLog.messageId))
                            body("$prefix.type", equalTo(chatReplyLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatReplyLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatReplyLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatReplyLog.roomSequence.sequence))
                            body("$prefix.parentMessageId", equalTo(chatReplyLog.parentMessageId))
                            body("$prefix.parentSeqNumber", equalTo(chatReplyLog.parentSeqNumber))
                            body("$prefix.parentMessageText", equalTo(chatReplyLog.parentMessageText))
                            body("$prefix.parentMessageType", equalTo(chatReplyLog.parentMessageType.toString().lowercase()))
                            body("$prefix.text", equalTo(chatReplyLog.text))
                        }
                        else -> {}
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    queryParameters(
                        parameterWithName("keyword").description("키워드"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),

                        // 공통 필드
                        fieldWithPath("data.chatLogs[].messageId").description("메시지 ID (모든 타입 공통)"),
                        fieldWithPath("data.chatLogs[].type").description("메시지 타입: normal, file, reply, invite, leave"),
                        fieldWithPath("data.chatLogs[].senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.chatLogs[].timestamp").description("보낸 시간(yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("data.chatLogs[].seqNumber").description("시퀀스 번호"),

                        // normal/ reply 등에서 쓰이는 text
                        fieldWithPath("data.chatLogs[].text")
                            .optional()
                            .description("메시지 내용 (normal, reply 타입에서 사용)"),

                        // reply 전용
                        fieldWithPath("data.chatLogs[].parentMessageId")
                            .optional()
                            .description("부모 메시지 ID (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentSeqNumber")
                            .optional()
                            .description("부모 메시지 시퀀스 번호 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageText")
                            .optional()
                            .description("부모 메시지 내용 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageType")
                            .optional()
                            .description("부모 메시지 타입 (reply 타입에서만 사용)"),

                        // file 전용
                        fieldWithPath("data.chatLogs[].files[].fileType")
                            .optional()
                            .description("파일 타입 (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].fileUrl")
                            .optional()
                            .description("파일 URL (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].index")
                            .optional()
                            .description("파일 인덱스 (file 메시지에서만 사용)"),
                    ),
                ),
            )
    }

    @Test
    fun searchDirectChatLogs() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatFileLog = TestDataFactory.createFileLog(chatRoomId)
        val chatReplyLog = TestDataFactory.createReplyLog(chatRoomId)
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)

        val chatLogs = listOf(
            chatFileLog,
            chatReplyLog,
            chatNormalLog,
        )
        every { directChatFacade.searchChatLog(any(), any(), any()) } returns chatLogs
        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("keyword", "keyword")
            .get("/api/chatRoom/{chatRoomId}/direct/log/search", chatRoomId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                chatLogs.forEachIndexed {
                        index, chatLog ->
                    val prefix = "data.chatLogs[$index]"
                    when (chatLog) {
                        is ChatFileLog -> {
                            body("$prefix.messageId", equalTo(chatFileLog.messageId))
                            body("$prefix.type", equalTo(chatFileLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatFileLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatFileLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatFileLog.roomSequence.sequence))
                            chatFileLog.medias.forEachIndexed {
                                    mediaIndex, media ->
                                val mediaPrefix = "$prefix.files[$mediaIndex]"
                                body("$mediaPrefix.fileType", equalTo(media.type.value()))
                                body("$mediaPrefix.fileUrl", equalTo(media.url))
                                body("$mediaPrefix.index", equalTo(media.index))
                            }
                        }
                        is ChatNormalLog -> {
                            body("$prefix.messageId", equalTo(chatNormalLog.messageId))
                            body("$prefix.type", equalTo(chatNormalLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatNormalLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatNormalLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatNormalLog.roomSequence.sequence))
                            body("$prefix.text", equalTo(chatNormalLog.text))
                        }
                        is ChatReplyLog -> {
                            body("$prefix.messageId", equalTo(chatReplyLog.messageId))
                            body("$prefix.type", equalTo(chatReplyLog.type.name.lowercase()))
                            body("$prefix.senderId", equalTo(chatReplyLog.senderId.id))
                            body("$prefix.timestamp", equalTo(chatReplyLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                            body("$prefix.seqNumber", equalTo(chatReplyLog.roomSequence.sequence))
                            body("$prefix.parentMessageId", equalTo(chatReplyLog.parentMessageId))
                            body("$prefix.parentSeqNumber", equalTo(chatReplyLog.parentSeqNumber))
                            body("$prefix.parentMessageText", equalTo(chatReplyLog.parentMessageText))
                            body("$prefix.parentMessageType", equalTo(chatReplyLog.parentMessageType.toString().lowercase()))
                            body("$prefix.text", equalTo(chatReplyLog.text))
                        }
                        else -> {}
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    queryParameters(
                        parameterWithName("keyword").description("키워드"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),

                        // 공통 필드
                        fieldWithPath("data.chatLogs[].messageId").description("메시지 ID (모든 타입 공통)"),
                        fieldWithPath("data.chatLogs[].type").description("메시지 타입: normal, file, reply, invite, leave"),
                        fieldWithPath("data.chatLogs[].senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.chatLogs[].timestamp").description("보낸 시간(yyyy-MM-dd HH:mm:ss)"),
                        fieldWithPath("data.chatLogs[].seqNumber").description("시퀀스 번호"),

                        // normal/ reply 등에서 쓰이는 text
                        fieldWithPath("data.chatLogs[].text")
                            .optional()
                            .description("메시지 내용 (normal, reply 타입에서 사용)"),

                        // reply 전용
                        fieldWithPath("data.chatLogs[].parentMessageId")
                            .optional()
                            .description("부모 메시지 ID (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentSeqNumber")
                            .optional()
                            .description("부모 메시지 시퀀스 번호 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageText")
                            .optional()
                            .description("부모 메시지 내용 (reply 타입에서만 사용)"),
                        fieldWithPath("data.chatLogs[].parentMessageType")
                            .optional()
                            .description("부모 메시지 타입 (reply 타입에서만 사용)"),

                        // file 전용
                        fieldWithPath("data.chatLogs[].files[].fileType")
                            .optional()
                            .description("파일 타입 (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].fileUrl")
                            .optional()
                            .description("파일 URL (file 메시지에서만 사용)"),
                        fieldWithPath("data.chatLogs[].files[].index")
                            .optional()
                            .description("파일 인덱스 (file 메시지에서만 사용)"),
                    ),
                ),
            )
    }
}
