package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.user.UserController
import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.service.user.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ActiveProfiles("test")
class UserControllerTest : RestDocsTest() {

    private lateinit var userService: UserService
    private lateinit var accountFacade: AccountFacade

    @BeforeEach
    fun setUp() {
        userService = mockk()
        accountFacade = mockk()
        val userController = UserController(userService, accountFacade)
        mockMvc = mockController(userController)
    }

    @Test
    @DisplayName("프로필 이미지 변경")
    fun changeProfileImage() {
        val mockFile = MockMultipartFile(
            "file",
            "testFile.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "Test content".toByteArray(),
        )

        every { userService.updateFile(any(), any(), any()) } just Runs

        // When: 파일 업로드 요청을 보냄
        val result = mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/user/image")
                .file(mockFile)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("userId", "testUserId")
                .param("category", "PROFILE"),
        )
        // Then: 응답 코드와 메시지 검증
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 엑세싱")
    fun makeAccess() {
        // Given: 사용자 엑세스 요청
        val requestBody = UserRequest.UpdateProfile(
            firstName = "testFirstName",
            lastName = "testLastName",
            birth = "2021-01-01",
        )

        every { userService.makeAccess(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/user/access")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 이름 변경")
    fun updateName() {
        val requestBody = UserRequest.UpdateName(
            firstName = "testFirstName",
            lastName = "testLastName",
        )

        every { userService.updateName(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/user/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 삭제")
    fun deleteUser() {
        every { accountFacade.deleteAccount(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/user")
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 생일 변경")
    fun changeBirth() {
        val requestBody = UserRequest.UpdateBirth(
            birth = "2021-01-01",
        )

        every { userService.updateBirth(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/user/birth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }
}
