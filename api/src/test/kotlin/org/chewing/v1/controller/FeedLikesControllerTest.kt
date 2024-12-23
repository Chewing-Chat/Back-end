package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.feed.FeedLikesController
import org.chewing.v1.dto.request.feed.LikesRequest
import org.chewing.v1.service.feed.FeedLikesService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ActiveProfiles("test")
class FeedLikesControllerTest : RestDocsTest() {

    private lateinit var feedLikesService: FeedLikesService
    private lateinit var feedLikesController: FeedLikesController

    @BeforeEach
    fun setUp() {
        feedLikesService = mockk()
        feedLikesController = FeedLikesController(feedLikesService)
        mockMvc = mockController(feedLikesController)
    }

    @Test
    @DisplayName("피드 좋아요 추가")
    fun `addFeedLikes`() {
        val userId = "testUserId"
        val requestBody = LikesRequest.Add(
            feedId = "testFeedId",
        )
        every { feedLikesService.like(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/feed/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessCreateResponse(result)
    }

    @Test
    @DisplayName("피드 좋아요 취소")
    fun `deleteFeedLikes`() {
        val userId = "testUserId"
        val requestBody = LikesRequest.Delete(
            feedId = "testFeedId",
        )
        every { feedLikesService.unlike(any(), any(), any()) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/feed/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }
}
