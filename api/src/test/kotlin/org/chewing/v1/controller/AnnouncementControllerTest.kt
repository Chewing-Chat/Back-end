package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.announcement.AnnouncementController
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.service.announcement.AnnouncementService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class AnnouncementControllerTest : RestDocsTest() {

    private lateinit var announcementService: AnnouncementService
    private lateinit var announcementController: AnnouncementController
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        announcementService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        announcementController = AnnouncementController(announcementService)
        mockMvc = mockController(announcementController, exceptionHandler)
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    fun getAnnouncements() {
        val announcement = TestDataFactory.createAnnouncement()
        every { announcementService.readAnnouncements() } returns listOf(announcement)

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "userId")
            .header("Authorization", "Bearer accessToken")
            .get("/api/announcement/list")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("data.announcements[0].announcementId", equalTo(announcement.announcementId.id))
            .body("data.announcements[0].topic", equalTo(announcement.topic))
            .body("data.announcements[0].uploadTime", equalTo(announcement.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.announcements[].announcementId").description("공지사항 ID"),
                        fieldWithPath("data.announcements[].topic").description("공지사항 제목"),
                        fieldWithPath("data.announcements[].uploadTime")
                            .description("공지사항 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    @DisplayName("공지사항 상세 조회")
    fun getAnnouncement() {
        val announcement = TestDataFactory.createAnnouncement()
        every { announcementService.readAnnouncement(announcement.announcementId) } returns announcement

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "userId")
            .header("Authorization", "Bearer accessToken")
            .get("/api/announcement/{announcementId}", announcement.announcementId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .body("data.content", equalTo(announcement.content))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    pathParameters(
                        parameterWithName("announcementId").description("조회할 공지사항의 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.content").description("공지사항 내용"),
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 존재 하지 않는 에러")
    fun getAnnouncementNotFound() {
        val invalidId = "invalidId"
        every { announcementService.readAnnouncement(any()) } throws NotFoundException(ErrorCode.ANNOUNCEMENT_NOT_FOUND)

        val result = given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "userId")
            .header("Authorization", "Bearer accessToken")
            .get("/api/announcement/{announcementId}", invalidId)
            .then()
            .statusCode(HttpStatus.NOT_FOUND.value())
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    pathParameters(
                        parameterWithName("announcementId").description("조회할 공지사항의 ID"),
                    ),
                    responseErrorFields(HttpStatus.NOT_FOUND, ErrorCode.ANNOUNCEMENT_NOT_FOUND, "공지사항을 찾을 수 없습니다. 잘못된 아이디를 보냈다면 생기는 문제입니다."),
                    requestAccessTokenFields(),
                ),
            )
        performErrorResponse(result, HttpStatus.NOT_FOUND, ErrorCode.ANNOUNCEMENT_NOT_FOUND)
    }
}
