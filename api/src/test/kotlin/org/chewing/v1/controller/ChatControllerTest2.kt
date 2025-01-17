package org.chewing.v1.controller

import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.chat.ChatController
import org.chewing.v1.facade.ChatFacade
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ChatControllerTest2 : RestDocsTest() {
    private lateinit var chatFacade: ChatFacade
    private lateinit var chatController: ChatController
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        chatFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        chatController = ChatController(chatFacade)
        mockMvc = mockController(chatController, exceptionHandler)
    }

//    @Test
//    @DisplayName("채팅방 파일 추가 테스트")
//    fun `uploadFiles`() {
//        val mockFile1 = MockMultipartFile(
//            "files",
//            "0.jpg",
//            MediaType.IMAGE_JPEG_VALUE,
//            "Test content".toByteArray(),
//        )
//        val mockFile2 = MockMultipartFile(
//            "files",
//            "1.jpg",
//            MediaType.IMAGE_JPEG_VALUE,
//            "Test content".toByteArray(),
//        )
//
//        every { chatFacade.processFiles(any(), any(), any()) } just Runs
//
//        // When: 파일 업로드 요청을 보냄
//        val result = mockMvc.perform(
//            MockMvcRequestBuilders.multipart("/api/chat/file/upload")
//                .file(mockFile1)
//                .file(mockFile2)
//                .contentType(MediaType.MULTIPART_FORM_DATA)
//                .requestAttr("userId", "testUserId")
//                .param("chatRoomId", "testRoomId"),
//
//        )
//        performCommonSuccessCreateResponse(result)
//    }
}
