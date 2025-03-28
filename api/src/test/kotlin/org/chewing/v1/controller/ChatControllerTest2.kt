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
import org.chewing.v1.TestDataFactory.createValidJpegMockFile
import org.chewing.v1.controller.chat.ChatController
import org.chewing.v1.facade.DirectChatFacade
import org.chewing.v1.facade.GroupChatFacade
import org.chewing.v1.model.user.UserId
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ChatControllerTest2 : RestDocsTest() {
    private lateinit var directChatFacade: DirectChatFacade
    private lateinit var groupChatFacade: GroupChatFacade
    private lateinit var chatController: ChatController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver

    @BeforeEach
    fun setUp() {
        directChatFacade = mockk()
        groupChatFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        chatController = ChatController(directChatFacade, groupChatFacade)
        mockMvc = mockController(chatController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("개인 채팅방 파일 추가")
    fun uploadDirectChatRoomFiles() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        every { directChatFacade.processDirectChatFiles(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("chatRoomId", "testChatRoomId")
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/chat/direct/file/upload")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("status", equalTo(201))
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
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    @DisplayName("그룹 채팅방 파일 추가")
    fun uploadGroupChatRoomFiles() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        every { groupChatFacade.processGroupChatFiles(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("chatRoomId", "testChatRoomId")
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/chat/group/file/upload")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("status", equalTo(201))
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
                        parameterWithName("chatRoomId").description("채팅방 ID"),
                    ),
                    responseSuccessFields(),
                ),
            )
    }
}
