package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory.createFeed
import org.chewing.v1.controller.feed.FeedController
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.facade.FeedFacade
import org.chewing.v1.model.feed.FeedStatus
import org.chewing.v1.service.feed.FeedService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class FeedControllerTest : RestDocsTest() {
    private lateinit var feedService: FeedService
    private lateinit var feedFacade: FeedFacade
    private lateinit var feedController: FeedController

    @BeforeEach
    fun setUp() {
        feedFacade = mockk()
        feedService = mockk()
        feedController = FeedController(feedService, feedFacade)
        mockMvc = mockController(feedController)
    }

    @Test
    @DisplayName("친구 피드 가져오기")
    fun `getFriendFeeds`() {
        val testFriendId = "testFriendId"
        val userId = "testUserId"
        val feed = createFeed()
        every { feedService.getOwnedFeeds(testFriendId, FeedStatus.NOT_HIDDEN) } returns listOf(feed)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/friend/$testFriendId/feed/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feeds[0].feedId").value(feed.feed.feedId))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].mainDetailFileUrl")
                    .value(feed.feedDetails[0].media.url),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].type")
                    .value(feed.feedDetails[0].media.type.value().lowercase()),
            )
    }

    @Test
    @DisplayName("내 피드 가져오기")
    fun `getOwnedFeeds`() {
        val userId = "testUserId"
        val feed = createFeed()
        every { feedService.getOwnedFeeds(userId, FeedStatus.NOT_HIDDEN) } returns listOf(feed)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/my/feed/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feeds[0].feedId").value(feed.feed.feedId))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].mainDetailFileUrl")
                    .value(feed.feedDetails[0].media.url),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].type")
                    .value(feed.feedDetails[0].media.type.value().lowercase()),
            )
    }

    @Test
    @DisplayName("친구 피드 상세 가져오기")
    fun `getFriendFeed`() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        val feed = createFeed()
        val uploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        every { feedFacade.getOwnedFeed(userId, testFeedId) } returns Pair(feed, true)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/friend/feed/$testFeedId/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feedId").value(feed.feed.feedId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.liked").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.topic").value(feed.feed.topic))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[0].index").value(feed.feedDetails[0].media.index))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[0].fileUrl").value(feed.feedDetails[0].media.url))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.uploadTime").value(uploadTime))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.details[0].type")
                    .value(feed.feedDetails[0].media.type.value().lowercase()),
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[1].index").value(feed.feedDetails[1].media.index))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[1].fileUrl").value(feed.feedDetails[1].media.url))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.details[1].type")
                    .value(feed.feedDetails[1].media.type.value().lowercase()),
            )
    }

    @Test
    @DisplayName("내 피드 상세 가져오기")
    fun `getOwnedFeed`() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        val feed = createFeed()
        every { feedFacade.getOwnedFeed(userId, testFeedId) } returns Pair(feed, true)
        val uploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/my/feed/$testFeedId/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feedId").value(feed.feed.feedId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.liked").value(true))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.topic").value(feed.feed.topic))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.likes").value(feed.feed.likes))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.comments").value(feed.feed.comments))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[0].index").value(feed.feedDetails[0].media.index))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[0].fileUrl").value(feed.feedDetails[0].media.url))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.uploadTime").value(uploadTime))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.details[0].type")
                    .value(feed.feedDetails[0].media.type.value().lowercase()),
            )
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[1].index").value(feed.feedDetails[1].media.index))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.details[1].fileUrl").value(feed.feedDetails[1].media.url))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.details[1].type")
                    .value(feed.feedDetails[1].media.type.value().lowercase()),
            )
    }

    @Test
    @DisplayName("숨긴 피드 가져오기")
    fun `getHiddenFeeds`() {
        val userId = "testUserId"
        val feed = createFeed()
        every { feedService.getOwnedFeeds(userId, FeedStatus.HIDDEN) } returns listOf(feed)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/hide")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.feeds[0].feedId").value(feed.feed.feedId))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].mainDetailFileUrl")
                    .value(feed.feedDetails[0].media.url),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.feeds[0].type")
                    .value(feed.feedDetails[0].media.type.value().lowercase()),
            )
    }

    @Test
    @DisplayName("피드 삭제")
    fun `deleteFeeds`() {
        val userId = "testUserId"
        val requestBody = listOf(
            FeedRequest.Delete(
                feedId = "testFeedId",
            ),
            FeedRequest.Delete(
                feedId = "testFeedId2",
            ),
        )
        every { feedFacade.removesFeed(userId, requestBody.map { it.toFeedId() }) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/feed")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("피드 추가")
    fun `addFeed`() {
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

        val feedId = "testFeedId"

        every { feedService.make(any(), any(), any(), any()) } returns feedId

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/feed")
                .file(mockFile1)
                .file(mockFile2)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("userId", "testUserId")
                .param("topic", "testTopic"),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.feedId").value(feedId))
    }

    @Test
    @DisplayName("피드 숨김")
    fun `hideFeeds`() {
        val userId = "testUserId"
        val requestBody = listOf(
            FeedRequest.Hide(
                feedId = "testFeedId",
            ),
            FeedRequest.Hide(
                feedId = "testFeedId2",
            ),
        )
        every { feedService.changeHide(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/feed/hide")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("피드 숨김 해제")
    fun `unHideFeeds`() {
        val userId = "testUserId"
        val requestBody = listOf(
            FeedRequest.Hide(
                feedId = "testFeedId",
            ),
            FeedRequest.Hide(
                feedId = "testFeedId2",
            ),
        )
        every { feedService.changeHide(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/feed/hide")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId)
                .content(jsonBody(requestBody)),
        )
        performCommonSuccessResponse(result)
    }
}
