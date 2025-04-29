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
import org.chewing.v1.controller.report.ReportController
import org.chewing.v1.dto.request.report.ReportRequest
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.report.ReportService
import org.chewing.v1.util.converter.StringToFeedTypeConverter
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class ReportControllerTest : RestDocsTest() {

    private lateinit var reportService: ReportService
    private lateinit var reportController: ReportController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver
    private lateinit var feedTypeConverter: StringToFeedTypeConverter

    @BeforeEach
    fun setUp() {
        reportService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        feedTypeConverter = StringToFeedTypeConverter()
        reportController = ReportController(reportService)
        mockMvc = mockController(reportController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("피드 신고 API 테스트")
    fun reportFeed() {
        val requestBody = ReportRequest.Feed(
            feedId = "testFeedId",
            reason = "신고 사유",
        )

        every { reportService.reportFeed(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .post("/api/report/feed")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("feedId").description("신고할 피드 ID"),
                        fieldWithPath("reason").description("신고 사유"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }
}
