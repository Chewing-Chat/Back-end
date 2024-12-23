package org.chewing.v1.controller

import io.mockk.every
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.announcement.AnnouncementController
import org.chewing.v1.service.announcement.AnnouncementService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class AnnouncementControllerTest : RestDocsTest() {

    private lateinit var announcementService: AnnouncementService
    private lateinit var announcementController: AnnouncementController

    @BeforeEach
    fun setUp() {
        announcementService = mockk()
        announcementController = AnnouncementController(announcementService)
        mockMvc = mockController(announcementController)
    }

    @Test
    @DisplayName("공지사항 목록 조회")
    fun getAnnouncements() {
        val announcement = TestDataFactory.createAnnouncement()
        every { announcementService.readAnnouncements() } returns listOf(announcement)
        val uploadTime = announcement.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/announcement/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId"),
        ).andDo(
            MockMvcRestDocumentation.document(
                "{class-name}/{method-name}",
                RestDocsUtils.requestPreprocessor(),
                RestDocsUtils.responsePreprocessor(),
                PayloadDocumentation.responseFields(
                    PayloadDocumentation.fieldWithPath("status").description("상태 코드"),
                    PayloadDocumentation.fieldWithPath("data.announcements[].announcementId").description("공지사항 ID"),
                    PayloadDocumentation.fieldWithPath("data.announcements[].topic").description("공지사항 제목"),
                    PayloadDocumentation.fieldWithPath("data.announcements[].uploadTime")
                        .description("공지사항 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                ),
            ),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.announcements[0].announcementId").value(announcement.id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.announcements[0].topic").value(announcement.topic))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.announcements[0].uploadTime").value(uploadTime))
    }

    @Test
    @DisplayName("공지사항 조회")
    fun getAnnouncement() {
        val announcement = TestDataFactory.createAnnouncement()
        every { announcementService.readAnnouncement(announcement.id) } returns announcement
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/announcement/${announcement.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.content").value(announcement.content))
    }
}
