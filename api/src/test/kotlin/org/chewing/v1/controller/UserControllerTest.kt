package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory
import org.chewing.v1.TestDataFactory.createValidJpegMockFile
import org.chewing.v1.controller.user.UserController
import org.chewing.v1.dto.request.user.UserRequest
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.facade.AccountFacade
import org.chewing.v1.model.user.AccessStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.converter.StringToFileCategoryConverter
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
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
            UserArgumentResolver(),
        )
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("프로필 이미지 변경")
    fun changeProfileImage() {
        val mockFile = createValidJpegMockFile("0.jpg")

        every { userService.updateFile(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .assertCommonSuccessResponse()
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
        verify { userService.updateFile(any(), any(), any()) }
    }

    @Test
    fun changeProfileImageFailedFileNameCouldNotBeEmpty() {
        val mockFile = createValidJpegMockFile("")

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FILE_NAME_COULD_NOT_EMPTY,
                        "파일 이름을 넣어주세요.",
                    ),
                ),
            )
    }

    @Test
    fun changeProfileImageFailedNotSupportFileType() {
        val mockFile = createValidJpegMockFile("testFile.exe")

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.NOT_SUPPORT_FILE_TYPE,
                        "지원하지 않는 파일 형식입니다.",
                    ),
                ),
            )
    }

    @Test
    fun changeProfileImageFailedFileNameIncorrect() {
        val mockFile = createValidJpegMockFile("testFile.jpg")

        every { userService.updateFile(any(), any(), any()) } throws ConflictException(ErrorCode.FILE_NAME_INCORRECT)

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FILE_NAME_INCORRECT,
                        "파일 이름이 잘못되었습니다. 0.jpg .. 1.jpg .. 처럼 순서대로 넣어주세요.",
                    ),
                ),
            )
    }

    @Test
    fun changeProfileImageFailedFileConvertFailed() {
        val mockFile = createValidJpegMockFile("testFile.jpg")

        every { userService.updateFile(any(), any(), any()) } throws ConflictException(ErrorCode.FILE_CONVERT_FAILED)

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FILE_CONVERT_FAILED,
                        "파일 변환에 실패했습니다. - 파일이 손상되었거나, 기타오류.",
                    ),
                ),
            )
    }

    @Test
    fun changeProfileImageFailedFileUploadFailed() {
        val mockFile = createValidJpegMockFile("testFile.jpg")

        every { userService.updateFile(any(), any(), any()) } throws ConflictException(ErrorCode.FILE_UPLOAD_FAILED)

        given()
            .setupAuthenticatedMultipartRequest()
            .multiPart("file", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/user/image")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FILE_UPLOAD_FAILED,
                        "파일 업로드에 실패 했음. 서버 오류, 네트워크 오류.",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("사용자 삭제")
    fun deleteUser() {
        every { accountFacade.deleteAccount(any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .delete("/api/user")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
        verify { accountFacade.deleteAccount(any()) }
    }

    @Test
    @DisplayName("상태 메시지 변경")
    fun changeStatusMessage() {
        val requestBody = UserRequest.UpdateStatusMessage("testStatusMessage")
        every { userService.updateStatusMessage(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/user/status/message")
            .then()
            .assertCommonSuccessResponse()
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
        verify { userService.updateStatusMessage(any(), any()) }
    }

    @Test
    @DisplayName("프로필 조회")
    fun getProfile() {
        val user = TestDataFactory.createUser("userId", AccessStatus.ACCESS)
        every { userService.getUser(any(), AccessStatus.ACCESS) } returns user

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/user/profile")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.name", equalTo(user.info.name))
            .body("data.phoneNumber", equalTo(user.localPhoneNumber.number))
            .body("data.countryCode", equalTo(user.localPhoneNumber.countryCode))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.name").description("사용자 이름"),
                        fieldWithPath("data.phoneNumber").description("전화번호"),
                        fieldWithPath("data.countryCode").description("국가 코드"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("푸시 알림 설정 조회")
    fun getPushNotification() {
        val deviceId = "testDeviceId"
        val pushInfo = TestDataFactory.createPushInfo(deviceId)
        every { userService.getPushInfo(any(), deviceId) } returns pushInfo

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/user/push/notification/$deviceId")
            .then()
            .statusCode(200)
            .body("status", equalTo(200))
            .body("data.deviceId", equalTo(deviceId))
            .body("data.scheduleStatus", equalTo(pushInfo.statusInfo.scheduleStatus.name.lowercase()))
            .body("data.chatStatus", equalTo(pushInfo.statusInfo.chatStatus.name.lowercase()))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.deviceId").description("디바이스 ID"),
                        fieldWithPath("data.scheduleStatus").description("일정 알림 상태"),
                        fieldWithPath("data.chatStatus").description("채팅 알림 상태"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("푸시 일정 알림 설정 변경")
    fun updatePushNotificationSchedule() {
        val deviceId = "testDeviceId"
        val requestBody = UserRequest.UpdateNotification(false, "testDeviceId")
        every { userService.updatePushNotification(any(), deviceId, any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/user/push/notification/chat")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("status").description("푸시 알림 상태"),
                        fieldWithPath("deviceId").description("디바이스 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
        verify { userService.updatePushNotification(any(), deviceId, any(), any()) }
    }

    @Test
    @DisplayName("푸시 채팅 알림 설정 변경")
    fun updatePushNotificationChat() {
        val deviceId = "testDeviceId"
        val requestBody = UserRequest.UpdateNotification(true, "testDeviceId")
        every { userService.updatePushNotification(any(), deviceId, any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/user/push/notification/chat")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("status").description("푸시 알림 상태"),
                        fieldWithPath("deviceId").description("디바이스 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
        verify { userService.updatePushNotification(any(), deviceId, any(), any()) }
    }
}
