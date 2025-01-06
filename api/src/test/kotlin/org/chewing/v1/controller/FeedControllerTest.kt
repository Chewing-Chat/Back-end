package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory.createFeed
import org.chewing.v1.controller.feed.FeedController
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.service.feed.FeedService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class FeedControllerTest : RestDocsTest() {
    private lateinit var feedService: FeedService
    private lateinit var feedController: FeedController

    @BeforeEach
    fun setUp() {
        feedService = mockk()
        feedController = FeedController(feedService)
        mockMvc = mockController(feedController)
    }

    @Test
    @DisplayName("내 피드 가져오기 - 썸네일만")
    fun `getOwnedFeeds`() {
        val userId = "testUserId"
        val feeds = listOf(createFeed())
        val uploadTime = feeds[0].feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        every { feedService.getFeeds(userId, userId) } returns feeds
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/owned/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        ).andExpect { result ->
            status().isOk
            jsonPath("$.status").value(200)
            feeds.forEachIndexed { index, feed ->
                jsonPath("$.data.feeds[$index].feedId").value(feed.feed.feedId)
                jsonPath("$.data.feeds[$index].topic").value(feed.feed.topic)
                jsonPath("$.data.feeds[$index].uploadTime").value(uploadTime)
                feed.feedDetails.forEachIndexed { detailIndex, feedDetail ->
                    jsonPath("$.data.feeds[$index].details[$detailIndex].index").value(feedDetail.media.index)
                    jsonPath("$.data.feeds[$index].details[$detailIndex].fileUrl").value(feedDetail.media.url)
                    jsonPath("$.data.feeds[$index].details[$detailIndex].type")
                        .value(feedDetail.media.type.value().lowercase())
                }
            }
        }
    }

    @Test
    @DisplayName("친구 피드 가져오기 - 썸네일만")
    fun `getFriendFeeds`() {
        val userId = "testUserId"
        val friendId = "testFriendId"
        val feeds = listOf(createFeed())
        val uploadTime = feeds[0].feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        every { feedService.getFeeds(userId, friendId) } returns feeds
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/friend/$friendId/list")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        ).andExpect { result ->
            status().isOk
            jsonPath("$.status").value(200)
            feeds.forEachIndexed { index, feed ->
                jsonPath("$.data.feeds[$index].feedId").value(feed.feed.feedId)
                jsonPath("$.data.feeds[$index].topic").value(feed.feed.topic)
                jsonPath("$.data.feeds[$index].uploadTime").value(uploadTime)
                feed.feedDetails.forEachIndexed { detailIndex, feedDetail ->
                    jsonPath("$.data.feeds[$index].details[$detailIndex].index").value(feedDetail.media.index)
                    jsonPath("$.data.feeds[$index].details[$detailIndex].fileUrl").value(feedDetail.media.url)
                    jsonPath("$.data.feeds[$index].details[$detailIndex].type")
                        .value(feedDetail.media.type.value().lowercase())
                }
            }
        }
    }

    @Test
    @DisplayName("피드 상세 가져오기")
    fun `getFeed`() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        val feed = createFeed()
        val uploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        every { feedService.getFeed(testFeedId, userId) } returns feed
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/$testFeedId/detail")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", userId),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.feedId").value(feed.feed.feedId))
            .andExpect(jsonPath("$.data.topic").value(feed.feed.topic))
            .andExpect(jsonPath("$.data.uploadTime").value(uploadTime))
        feed.feedDetails.forEachIndexed { index, feedDetail ->
            jsonPath("$.data.details[$index].index").value(feedDetail.media.index)
            jsonPath("$.data.details[$index].fileUrl").value(feedDetail.media.url)
            jsonPath("$.data.details[$index].type").value(feedDetail.media.type.value().lowercase())
        }
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
        every { feedService.removes(userId, requestBody.map { it.toFeedId() }) } just Runs
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
        val testFriendIds = listOf<String>("testFriendId")

        every { feedService.make(any(), any(), any(), any(), any()) } returns feedId

        mockMvc.perform(
            MockMvcRequestBuilders.multipart("/api/feed")
                .file(mockFile1)
                .file(mockFile2)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .requestAttr("userId", "testUserId")
                .param("topic", "testTopic")
                .param("friendIds", *testFriendIds.toTypedArray()),
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.data.feedId").value(feedId))
    }
}
