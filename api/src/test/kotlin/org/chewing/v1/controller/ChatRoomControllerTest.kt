package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.chat.ChatRoomController
import org.chewing.v1.dto.request.chat.ChatRoomRequest
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.chat.room.ChatRoomId
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.chat.DirectChatRoomService
import org.chewing.v1.service.chat.GroupChatRoomService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class ChatRoomControllerTest : RestDocsTest() {
    private lateinit var directChatFacade: DirectChatFacade
    private lateinit var groupChatFacade: GroupChatFacade
    private lateinit var chatRoomController: ChatRoomController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver
    private lateinit var groupChatRoomService: GroupChatRoomService
    private lateinit var directChatRoomService: DirectChatRoomService

    @BeforeEach
    fun setUp() {
        directChatFacade = mockk()
        groupChatFacade = mockk()
        directChatRoomService = mockk()
        groupChatRoomService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        chatRoomController =
            ChatRoomController(groupChatFacade, directChatFacade, groupChatRoomService, directChatRoomService)
        mockMvc = mockController(chatRoomController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    fun getChatRooms() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)

        val chatLog = chatNormalLog
        val userId = UserId.of("testUserId")
        val directChatRoom = TestDataFactory.createDirectChatRoom(chatRoomId)
        val directChatRooms = listOf(Pair(directChatRoom, chatLog))
        val groupChatRoom = TestDataFactory.createGroupChatRoom(chatRoomId)
        val groupChatRooms = listOf(Pair(groupChatRoom, chatLog))

        every { directChatFacade.processGetDirectChatRooms(any()) } returns directChatRooms
        every { groupChatFacade.processGroupChatRooms(any()) } returns groupChatRooms

        given()
            .setupAuthenticatedMultipartRequest()
            .get("/api/chatRoom/list")
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                directChatRooms.forEachIndexed { index, (chatRoom, chatLog) ->
                    val chatRoomJsonPath = "data.directChatRooms[$index]"
                    val chatLogJsonPath = "$chatRoomJsonPath.latestChatLog"
                    body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(chatRoom.roomSequence.sequenceNumber))
                    body("$chatRoomJsonPath.readSequenceNumber", equalTo(chatRoom.ownSequence.readSequenceNumber))
                    body("$chatRoomJsonPath.joinSequenceNumber", equalTo(chatRoom.ownSequence.joinSequenceNumber))
                    body("$chatRoomJsonPath.chatRoomId", equalTo(chatRoom.roomInfo.chatRoomId.id))
                    body("$chatLogJsonPath.messageId", equalTo(chatLog.messageId))
                    body("$chatLogJsonPath.type", equalTo(chatLog.type.name.lowercase()))
                    body("$chatLogJsonPath.senderId", equalTo(chatLog.senderId.id))
                    body(
                        "$chatLogJsonPath.timestamp",
                        equalTo(chatLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    )
                    body("$chatLogJsonPath.seqNumber", equalTo(chatLog.number.sequenceNumber))
                    body("$chatLogJsonPath.text", equalTo(chatLog.text))
                    body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(chatRoom.roomInfo.status.name.lowercase()))
                    body("$chatRoomJsonPath.friendId", equalTo(chatRoom.roomInfo.friendId.id))
                }
                groupChatRooms.forEachIndexed { index, (chatRoom, chatLog) ->
                    val chatRoomMemberStatus = chatRoom.memberInfos
                        .find { it.memberId == userId }!!
                    val chatRoomJsonPath = "data.groupChatRooms[$index]"
                    val chatLogJsonPath = "$chatRoomJsonPath.latestChatLog"
                    val friendIds = chatRoom.memberInfos
                        .filter { it.memberId != userId }
                        .map { it.memberId.id }
                    body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(chatRoom.roomSequence.sequenceNumber))
                    body("$chatRoomJsonPath.readSequenceNumber", equalTo(chatRoom.ownSequence.readSequenceNumber))
                    body("$chatRoomJsonPath.joinSequenceNumber", equalTo(chatRoom.ownSequence.joinSequenceNumber))
                    body("$chatRoomJsonPath.chatRoomId", equalTo(chatRoom.roomInfo.chatRoomId.id))
                    body("$chatLogJsonPath.messageId", equalTo(chatLog.messageId))
                    body("$chatLogJsonPath.type", equalTo(chatLog.type.name.lowercase()))
                    body("$chatLogJsonPath.senderId", equalTo(chatLog.senderId.id))
                    body(
                        "$chatLogJsonPath.timestamp",
                        equalTo(chatLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    )
                    body("$chatLogJsonPath.seqNumber", equalTo(chatLog.number.sequenceNumber))
                    body("$chatLogJsonPath.text", equalTo(chatLog.text))
                    body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(chatRoomMemberStatus.status.name.lowercase()))

                    friendIds.forEachIndexed { friendIndex, friendId ->
                        body("$chatRoomJsonPath.friendIds[$friendIndex]", equalTo(friendId))
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.directChatRooms").description("1:1 채팅방 목록"),
                        fieldWithPath("data.groupChatRooms").description("그룹 채팅방 목록"),
                        fieldWithPath("data.directChatRooms[].chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.directChatRooms[].chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].readSequenceNumber").description("읽은 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.messageId").description("채팅 메시지 ID"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.type").description("채팅 메시지 타입(Reply, File, Normal)"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.timestamp").description("보낸 시간"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.seqNumber").description("메시지 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.text").description("메시지 내용"),
                        fieldWithPath("data.directChatRooms[].chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.directChatRooms[].friendId").description("친구 ID"),
                        fieldWithPath("data.groupChatRooms[].chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.groupChatRooms[].chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].readSequenceNumber").description("읽은 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.messageId").description("채팅 메시지 ID"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.type").description("채팅 메시지 타입(Reply, Leave, Invite, File, Normal)"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.timestamp").description("보낸 시간"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.seqNumber").description("메시지 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.text").description("메시지 내용"),
                        fieldWithPath("data.groupChatRooms[].chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.groupChatRooms[].friendIds").description("친구 ID 목록(본인제외)"),
                    ),
                ),
            )
    }

    @Test
    fun searchChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val chatNormalLog = TestDataFactory.createNormalLog(chatRoomId)

        val chatLog = chatNormalLog
        val userId = UserId.of("testUserId")
        val directChatRoom = TestDataFactory.createDirectChatRoom(chatRoomId)
        val directChatRooms = listOf(Pair(directChatRoom, chatLog))
        val groupChatRoom = TestDataFactory.createGroupChatRoom(chatRoomId)
        val groupChatRooms = listOf(Pair(groupChatRoom, chatLog))
        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")
        every { directChatFacade.searchDirectChatRooms(any(), any()) } returns directChatRooms
        every { groupChatFacade.searchGroupChatRooms(any(), any()) } returns groupChatRooms

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .get("/api/chatRoom/search")
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                directChatRooms.forEachIndexed { index, (chatRoom, chatLog) ->
                    val chatRoomJsonPath = "data.directChatRooms[$index]"
                    val chatLogJsonPath = "$chatRoomJsonPath.latestChatLog"
                    body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(chatRoom.roomSequence.sequenceNumber))
                    body("$chatRoomJsonPath.readSequenceNumber", equalTo(chatRoom.ownSequence.readSequenceNumber))
                    body("$chatRoomJsonPath.joinSequenceNumber", equalTo(chatRoom.ownSequence.joinSequenceNumber))
                    body("$chatRoomJsonPath.chatRoomId", equalTo(chatRoom.roomInfo.chatRoomId.id))
                    body("$chatLogJsonPath.messageId", equalTo(chatLog.messageId))
                    body("$chatLogJsonPath.type", equalTo(chatLog.type.name.lowercase()))
                    body("$chatLogJsonPath.senderId", equalTo(chatLog.senderId.id))
                    body(
                        "$chatLogJsonPath.timestamp",
                        equalTo(chatLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    )
                    body("$chatLogJsonPath.seqNumber", equalTo(chatLog.number.sequenceNumber))
                    body("$chatLogJsonPath.text", equalTo(chatLog.text))
                    body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(chatRoom.roomInfo.status.name.lowercase()))
                    body("$chatRoomJsonPath.friendId", equalTo(chatRoom.roomInfo.friendId.id))
                }
                groupChatRooms.forEachIndexed { index, (chatRoom, chatLog) ->
                    val chatRoomMemberStatus = chatRoom.memberInfos
                        .find { it.memberId == userId }!!
                    val chatRoomJsonPath = "data.groupChatRooms[$index]"
                    val chatLogJsonPath = "$chatRoomJsonPath.latestChatLog"
                    val friendIds = chatRoom.memberInfos
                        .filter { it.memberId != userId }
                        .map { it.memberId.id }
                    body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(chatRoom.roomSequence.sequenceNumber))
                    body("$chatRoomJsonPath.readSequenceNumber", equalTo(chatRoom.ownSequence.readSequenceNumber))
                    body("$chatRoomJsonPath.joinSequenceNumber", equalTo(chatRoom.ownSequence.joinSequenceNumber))
                    body("$chatRoomJsonPath.chatRoomId", equalTo(chatRoom.roomInfo.chatRoomId.id))
                    body("$chatLogJsonPath.messageId", equalTo(chatLog.messageId))
                    body("$chatLogJsonPath.type", equalTo(chatLog.type.name.lowercase()))
                    body("$chatLogJsonPath.senderId", equalTo(chatLog.senderId.id))
                    body(
                        "$chatLogJsonPath.timestamp",
                        equalTo(chatLog.timestamp.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))),
                    )
                    body("$chatLogJsonPath.seqNumber", equalTo(chatLog.number.sequenceNumber))
                    body("$chatLogJsonPath.text", equalTo(chatLog.text))
                    body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(chatRoomMemberStatus.status.name.lowercase()))

                    friendIds.forEachIndexed { friendIndex, friendId ->
                        body("$chatRoomJsonPath.friendIds[$friendIndex]", equalTo(friendId))
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    queryParameters(
                        parameterWithName("friendIds").description("친구 ID 목록"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.directChatRooms").description("1:1 채팅방 목록"),
                        fieldWithPath("data.groupChatRooms").description("그룹 채팅방 목록"),
                        fieldWithPath("data.directChatRooms[].chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.directChatRooms[].chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].readSequenceNumber").description("읽은 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.messageId").description("채팅 메시지 ID"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.type").description("채팅 메시지 타입(Reply, File, Normal)"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.timestamp").description("보낸 시간"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.seqNumber").description("메시지 시퀀스 번호"),
                        fieldWithPath("data.directChatRooms[].latestChatLog.text").description("메시지 내용"),
                        fieldWithPath("data.directChatRooms[].chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.directChatRooms[].friendId").description("친구 ID"),
                        fieldWithPath("data.groupChatRooms[].chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.groupChatRooms[].chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].readSequenceNumber").description("읽은 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.messageId").description("채팅 메시지 ID"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.type").description("채팅 메시지 타입(Reply, Leave, Invite, File, Normal)"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.senderId").description("보낸 사람 ID"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.timestamp").description("보낸 시간"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.seqNumber").description("메시지 시퀀스 번호"),
                        fieldWithPath("data.groupChatRooms[].latestChatLog.text").description("메시지 내용"),
                        fieldWithPath("data.groupChatRooms[].chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.groupChatRooms[].friendIds").description("친구 ID 목록(본인제외)"),
                    ),
                ),
            )
    }

    @Test
    fun getDirectChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val friendId = UserId.of("testFriendId")
        val directChatRoom = TestDataFactory.createDirectChatRoom(chatRoomId)
        every { directChatFacade.processGetDirectChatRoom(any(), any()) } returns directChatRoom

        given()
            .setupAuthenticatedMultipartRequest()
            .get("/api/chatRoom/direct/{friendId}", friendId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(200))
            .apply {
                val chatRoomJsonPath = "data"
                body("$chatRoomJsonPath.chatRoomId", equalTo(directChatRoom.roomInfo.chatRoomId.id))
                body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(directChatRoom.roomSequence.sequenceNumber))
                body("$chatRoomJsonPath.readSequenceNumber", equalTo(directChatRoom.ownSequence.readSequenceNumber))
                body("$chatRoomJsonPath.joinSequenceNumber", equalTo(directChatRoom.ownSequence.joinSequenceNumber))
                body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(directChatRoom.roomInfo.status.name.lowercase()))
                body("$chatRoomJsonPath.friendId", equalTo(directChatRoom.roomInfo.friendId.id))
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("friendId").description("친구 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.readSequenceNumber").description("읽은 시퀀스 번호"),
                        fieldWithPath("data.joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.friendId").description("친구 ID"),
                    ),
                ),
            )
    }

    @Test
    fun createCommonDirectChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val commonMessage = "testMessage"
        val directChatRoom = TestDataFactory.createDirectChatRoom(chatRoomId)
        val requestBody = ChatRoomRequest.Create(
            friendId = "testFriendId",
            message = commonMessage,
        )
        every {
            directChatFacade.processCreateDirectChatRoomCommonChat(
                any(),
                any(),
                commonMessage,
            )
        } returns directChatRoom

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .post("/api/chatRoom/direct/create/common")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(201))
            .apply {
                val chatRoomJsonPath = "data"
                body("$chatRoomJsonPath.chatRoomId", equalTo(directChatRoom.roomInfo.chatRoomId.id))
                body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(directChatRoom.roomSequence.sequenceNumber))
                body("$chatRoomJsonPath.readSequenceNumber", equalTo(directChatRoom.ownSequence.readSequenceNumber))
                body("$chatRoomJsonPath.joinSequenceNumber", equalTo(directChatRoom.ownSequence.joinSequenceNumber))
                body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(directChatRoom.roomInfo.status.name.lowercase()))
                body("$chatRoomJsonPath.friendId", equalTo(directChatRoom.roomInfo.friendId.id))
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                        fieldWithPath("message").description("메시지"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.readSequenceNumber").description("읽은 시퀘스 번호"),
                        fieldWithPath("data.joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.friendId").description("친구 ID"),
                    ),
                ),
            )
    }

    @Test
    fun createFilesDirectChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val directChatRoom = TestDataFactory.createDirectChatRoom(chatRoomId)
        val mockFile1 = MockMultipartFile(
            "files",
            "0.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "Test content".toByteArray(),
        )
        val mockFile2 = MockMultipartFile(
            "files",
            "1.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "Test content".toByteArray(),
        )
        val testFriendId = "testFriendId"
        every { directChatFacade.processCreateDirectChatRoomFilesChat(any(), any(), any()) } returns directChatRoom

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("friendId", testFriendId)
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/chatRoom/direct/create/files")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(201))
            .apply {
                val chatRoomJsonPath = "data"
                body("$chatRoomJsonPath.chatRoomId", equalTo(directChatRoom.roomInfo.chatRoomId.id))
                body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(directChatRoom.roomSequence.sequenceNumber))
                body("$chatRoomJsonPath.readSequenceNumber", equalTo(directChatRoom.ownSequence.readSequenceNumber))
                body("$chatRoomJsonPath.joinSequenceNumber", equalTo(directChatRoom.ownSequence.joinSequenceNumber))
                body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(directChatRoom.roomInfo.status.name.lowercase()))
                body("$chatRoomJsonPath.friendId", equalTo(directChatRoom.roomInfo.friendId.id))
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestParts(
                        partWithName("files").description("채팅방에 추가할 이미지 파일 (image/jpeg)")
                            .description("채팅방에 추가할 이미지 파일 (image/jpeg) - 형식은 0.jpg, 1.jpg, ..."),
                    ),
                    queryParameters(
                        parameterWithName("friendId").description("친구 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.readSequenceNumber").description("읽은 시퀘스 번호"),
                        fieldWithPath("data.joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.friendId").description("친구 ID"),
                    ),
                ),
            )
    }

    @Test
    fun deleteDirectChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val requestBody = ChatRoomRequest.Delete(
            chatRoomId = chatRoomId.id,
        )
        every { directChatRoomService.deleteDirectChatRoom(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/chatRoom/direct/delete")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("chatRoomId").description("채팅방 ID"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun favoriteDirectChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val favorite = true
        val requestBody = ChatRoomRequest.Favorite(
            chatRoomId = chatRoomId.id,
            favorite = favorite,
        )
        every { directChatRoomService.favoriteDirectChatRoomType(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/chatRoom/direct/favorite")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("chatRoomId").description("채팅방 ID"),
                        fieldWithPath("favorite").description("즐겨찾기 여부"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun favoriteGroupChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val favorite = true
        val requestBody = ChatRoomRequest.Favorite(
            chatRoomId = chatRoomId.id,
            favorite = favorite,
        )
        every { groupChatRoomService.favoriteGroupChatRoomType(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/chatRoom/group/favorite")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("chatRoomId").description("채팅방 ID"),
                        fieldWithPath("favorite").description("즐겨찾기 여부"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun createGroupChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val name = "testName"
        val friendIds = listOf("testFriendId1", "testFriendId2")
        val userId = UserId.of("testUserId")
        val groupChatRoom = TestDataFactory.createGroupChatRoom(chatRoomId)
        val requestBody = ChatRoomRequest.CreateGroup(
            friendIds = friendIds,
            name = name,
        )
        every { groupChatFacade.processGroupChatCreate(any(), any(), any()) } returns groupChatRoom

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .post("/api/chatRoom/group/create")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body("status", equalTo(201))
            .apply {
                val friendIds = groupChatRoom.memberInfos
                    .filter { it.memberId != userId }
                    .map { it.memberId.id }
                val chatRoomMemberStatus = groupChatRoom.memberInfos
                    .find { it.memberId == userId }!!
                val chatRoomJsonPath = "data"
                body("$chatRoomJsonPath.chatRoomId", equalTo(groupChatRoom.roomInfo.chatRoomId.id))
                body("$chatRoomJsonPath.chatRoomName", equalTo(groupChatRoom.roomInfo.name))
                body("$chatRoomJsonPath.chatRoomSequenceNumber", equalTo(groupChatRoom.roomSequence.sequenceNumber))
                body("$chatRoomJsonPath.readSequenceNumber", equalTo(groupChatRoom.ownSequence.readSequenceNumber))
                body("$chatRoomJsonPath.joinSequenceNumber", equalTo(groupChatRoom.ownSequence.joinSequenceNumber))
                body("$chatRoomJsonPath.chatRoomOwnStatus", equalTo(chatRoomMemberStatus.status.name.lowercase()))
                friendIds.forEachIndexed { friendIndex, friendId ->
                    body("$chatRoomJsonPath.friendIds[$friendIndex]", equalTo(friendId))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("friendIds").description("친구 ID 목록"),
                        fieldWithPath("name").description("그룹 채팅방 이름"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("응답 상태 코드"),
                        fieldWithPath("data.chatRoomId").description("채팅방 ID"),
                        fieldWithPath("data.chatRoomSequenceNumber").description("채팅방 시퀀스 번호"),
                        fieldWithPath("data.readSequenceNumber").description("읽은 시퀘스 번호"),
                        fieldWithPath("data.joinSequenceNumber").description("참여한 시퀀스 번호"),
                        fieldWithPath("data.chatRoomOwnStatus").description("채팅방 멤버 상태"),
                        fieldWithPath("data.friendIds").description("친구 ID 목록"),
                        fieldWithPath("data.chatRoomName").description("채팅방 이름"),
                    ),
                ),
            )
    }

    @Test
    fun inviteGroupChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val friendId = "testFriendId"
        val requestBody = ChatRoomRequest.Invite(
            chatRoomId = chatRoomId.id,
            friendId = friendId,
        )
        every { groupChatFacade.processGroupChatInvite(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .post("/api/chatRoom/group/invite")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("chatRoomId").description("채팅방 ID"),
                        fieldWithPath("friendId").description("친구 ID"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun leaveGroupChatRoom() {
        val chatRoomId = ChatRoomId.of("testChatRoomId")
        val requestBody = ChatRoomRequest.Leave(
            chatRoomId = chatRoomId.id,
        )
        every { groupChatFacade.processGroupChatLeave(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/chatRoom/group/leave")
            .then()
            .statusCode(HttpStatus.OK.value())
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("chatRoomId").description("채팅방 ID"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }
}
