package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestJwtTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.announcement.AnnouncementController
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.service.announcement.AnnouncementService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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
        val uploadTime = announcement.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        mockMvc.perform(
            get("/api/announcement/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId")
                .header("Authorization", "Bearer sample-token"),
        ).andDo(
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
                requestJwtTokenFields(),
            ),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.announcements[0].announcementId").value(announcement.id))
            .andExpect(jsonPath("$.data.announcements[0].topic").value(announcement.topic))
            .andExpect(jsonPath("$.data.announcements[0].uploadTime").value(uploadTime))
    }

    @Test
    @DisplayName("공지사항 상세 조회")
    fun getAnnouncement() {
        val announcement = TestDataFactory.createAnnouncement()
        every { announcementService.readAnnouncement(announcement.id) } returns announcement
        mockMvc.perform(
            get("/api/announcement/${announcement.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId")
                .header("Authorization", "Bearer sample-token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.content").description("공지사항 내용"),
                    ),
                    requestJwtTokenFields(),
                ),
            )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.content").value(announcement.content))
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 존재 하지 않는 에러")
    fun getAnnouncementNotFound() {
        val invalidId = "invalidId"
        every { announcementService.readAnnouncement(any()) } throws NotFoundException(ErrorCode.ANNOUNCEMENT_NOT_FOUND)

        val result = mockMvc.perform(
            get("/api/announcement/$invalidId")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId")
                .header("Authorization", "Bearer sample-token"),
        )
            .andDo(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(HttpStatus.NOT_FOUND, ErrorCode.ANNOUNCEMENT_NOT_FOUND, "공지사항을 찾을 수 없습니다. 잘못된 아이디를 보냈다면 생기는 문제입니다."),
                    requestJwtTokenFields(),
                ),
            )
        performErrorResponse(result, HttpStatus.NOT_FOUND, ErrorCode.ANNOUNCEMENT_NOT_FOUND)
    }
}
