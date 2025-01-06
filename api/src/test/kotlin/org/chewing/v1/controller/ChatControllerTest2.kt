package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.chat.ChatController
import org.chewing.v1.facade.ChatFacade
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ActiveProfiles("test")
class ChatControllerTest2 : RestDocsTest() {
    private lateinit var chatFacade: ChatFacade
    private lateinit var chatController: ChatController

    @BeforeEach
    fun setUp() {
        chatFacade = mockk()
        chatController = ChatController(chatFacade)
        mockMvc = mockController(chatController)
    }

    @Test
    @DisplayName("채팅방 파일 추가 테스트")
    fun `uploadFiles`() {
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

        every { chatFacade.processFiles(any(), any(), any()) } just Runs

        // When: 파일 업로드 요청을 보냄
        val result = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/chat/file/upload")
                .file(mockFile1)
                .file(mockFile2)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("userId", "testUserId")
                .param("chatRoomId", "testRoomId"),

        )
        performCommonSuccessCreateResponse(result)
    }
}
