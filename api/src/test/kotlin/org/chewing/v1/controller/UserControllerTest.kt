package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.user.UserController
import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.converter.StringToFileCategoryConverter
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class UserControllerTest : RestDocsTest() {

    private lateinit var userService: UserService
    private lateinit var accountFacade: AccountFacade

    @BeforeEach
    fun setUp() {
        userService = mockk()
        accountFacade = mockk()
        val userController = UserController(userService, accountFacade)
        mockMvc = mockControllerWithAdviceAndCustomConverter(
            userController,
            GlobalExceptionHandler(),
            StringToFileCategoryConverter(),
        )
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

        val result = given()
            .header("Authorization", "Bearer accessToken")
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .attribute("userId", "testUserId")
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestParts(
                        partWithName("file").description("프로필 이미지 파일 (image/jpeg)"),
                    ),
                    requestHeaders(
                        headerWithName("Content-Type").description("멀티파트 폼 데이터 타입 (image/*)"),
                        headerWithName("Authorization").description("액세스 토큰"),
                    ),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessResponse(result)
        verify { userService.updateFile(any(), any(), any()) }
    }

    @Test
    @DisplayName("사용자 삭제")
    fun deleteUser() {
        every { accountFacade.deleteAccount(any()) } just Runs

        val result =
            given()
                .header("Authorization", "Bearer accessToken")
                .attribute("userId", "testUserId")
                .delete("/api/user")
                .then()
                .apply(
                    document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestAccessTokenFields(),
                        responseSuccessFields(),
                    ),
                )
        performCommonSuccessResponse(result)

        verify { accountFacade.deleteAccount(any()) }
    }

    @Test
    @DisplayName("상태 메시지 변경")
    fun changeStatusMessage() {
        val requestBody = UserRequest.UpdateStatusMessage("testStatusMessage")
        every { userService.updateStatusMessage(any(), any()) } just Runs

        val result =
            given()
                .header("Authorization", "Bearer accessToken")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(requestBody)
                .attribute("userId", "testUserId")
                .put("/api/user/status/message")
                .then()
                .apply(
                    document(
                        "{class-name}/{method-name}",
                        requestPreprocessor(),
                        responsePreprocessor(),
                        requestFields(
                            fieldWithPath("statusMessage").description("상태 메시지"),
                        ),
                        requestAccessTokenFields(),
                        responseSuccessFields(),
                    ),
                )

        performCommonSuccessResponse(result)

        verify { userService.updateStatusMessage(any(), any()) }
    }

    @Test
    @DisplayName("프로필 조회")
    fun getProfile() {
        val user = TestDataFactory.createUser(AccessStatus.ACCESS)
        every { userService.getUser(any()) } returns user

        given()
            .header("Authorization", "Bearer accessToken")
            .attribute("userId", "testUserId")
            .get("/api/user/profile")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.name", equalTo(user.name))
            .body("data.phoneNumber", equalTo(user.phoneNumber.number))
            .body("data.countryCode", equalTo(user.phoneNumber.countryCode))
            .body("data.birth", equalTo(user.birth))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.name").description("사용자 이름"),
                        fieldWithPath("data.birth").description("생년월일"),
                        fieldWithPath("data.phoneNumber").description("전화번호"),
                        fieldWithPath("data.countryCode").description("국가 코드"),
                    ),
                ),
            )
    }
}
