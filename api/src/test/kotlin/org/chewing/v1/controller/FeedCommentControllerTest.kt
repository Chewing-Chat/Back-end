package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.feed.FeedCommentController
import org.chewing.v1.dto.request.feed.CommentRequest
import org.chewing.v1.facade.FeedFacade
import org.chewing.v1.service.feed.FeedCommentService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class FeedCommentControllerTest : RestDocsTest() {
    private lateinit var feedCommentService: FeedCommentService
    private lateinit var feedFacade: FeedFacade
    private lateinit var feedCommentController: FeedCommentController

    @BeforeEach
    fun setUp() {
        feedFacade = mockk()
        feedCommentService = mockk()
        feedCommentController = FeedCommentController(feedCommentService, feedFacade)
        mockMvc = mockController(feedCommentController)
    }

    @Test
    @DisplayName("피드 댓글 추가")
    fun addFeedComment() {
        // Given
        val requestBody = CommentRequest.Add(
            feedId = "feedId",
            comment = "comment",
        )
        val commentId = "commentId"
        every { feedFacade.commentFeed(any(), any(), any(), any()) } returns commentId
        // When
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/feed/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId")
                .content(jsonBody(requestBody)),
        ).andExpect(
            status().isCreated,
        ).andExpect(
            jsonPath("$.data.commentId").value(commentId),
        )
    }

    @Test
    @DisplayName("피드 댓글 삭제")
    fun deleteFeedComment() {
        // Given
        val requestBody = listOf(
            CommentRequest.Delete(
                commentId = "commentId1",
                feedId = "feedId1",
            ),
            CommentRequest.Delete(
                commentId = "commentId2",
                feedId = "feedId2",
            ),
        )
        every { feedCommentService.remove(any(), any(), any()) } just Runs
        // When
        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/feed/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId")
                .content(jsonBody(requestBody)),
        )
        // Then
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("내 피드 댓글 조회")
    fun getFeedComments() {
        // Given
        val feedId = "feedId"

        val comment = TestDataFactory.createComment()
        val formattedCommentTime = comment.createAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        // When
        every { feedFacade.getFeedComment("userId", feedId) } returns listOf(comment, comment)
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/$feedId/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(
                jsonPath("$.data.comments[0].friend.friendId").value(comment.writer.userId),
            )
            .andExpect(
                jsonPath("$.data.comments[0].friend.firstName")
                    .value(comment.writer.name.firstName()),
            )
            .andExpect(
                jsonPath("$.data.comments[0].friend.lastName")
                    .value(comment.writer.name.lastName()),
            )
            .andExpect(
                jsonPath("$.data.comments[0].friend.imageUrl").value(comment.writer.image.url),
            )
            .andExpect(
                jsonPath("$.data.comments[0].friend.imageType")
                    .value(comment.writer.image.type.value().lowercase()),
            )
            .andExpect(
                jsonPath("$.data.comments[0].friend.access")
                    .value(comment.writer.status.name.lowercase()),
            )
            .andExpect(jsonPath("$.data.comments[0].comment.commentId").value(comment.id))
            .andExpect(jsonPath("$.data.comments[0].comment.comment").value(comment.comment))
            .andExpect(
                jsonPath("$.data.comments[0].comment.commentTime").value(formattedCommentTime),
            )
    }

    @Test
    @DisplayName("내가 댓글 단 피드 조회")
    fun getMyCommentedFeed() {
        val userId = "userId"
        val commentId = "commentId"
        val userCommentInfo = TestDataFactory.createUserCommentedInfo(commentId)
        val formattedCommentTime =
            userCommentInfo.comment.createAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        // When
        every { feedFacade.getUserCommented(userId) } returns listOf(userCommentInfo)

        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/feed/my/comment")
                .contentType(MediaType.APPLICATION_JSON)
                .requestAttr("userId", "userId"),
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(200))
            .andExpect(jsonPath("$.data.myComments[0].comments[0].commentId").value(commentId))
            .andExpect(jsonPath("$.data.myComments[0].comments[0].comment").value(userCommentInfo.comment.comment))
            .andExpect(jsonPath("$.data.myComments[0].comments[0].commentTime").value(formattedCommentTime))
            .andExpect(jsonPath("$.data.myComments[0].friend.friendId").value(userCommentInfo.friendShip.friendId))
            .andExpect(jsonPath("$.data.myComments[0].friend.firstName").value(userCommentInfo.friendShip.friendName.firstName()))
            .andExpect(jsonPath("$.data.myComments[0].friend.lastName").value(userCommentInfo.friendShip.friendName.lastName()))
            .andExpect(jsonPath("$.data.myComments[0].friend.imageUrl").value(userCommentInfo.user.image.url))
            .andExpect(
                jsonPath("$.data.myComments[0].friend.imageType").value(
                    userCommentInfo.user.image.type.value().lowercase(),
                ),
            )
            .andExpect(jsonPath("$.data.myComments[0].friend.access").value(userCommentInfo.user.status.name.lowercase()))
            .andExpect(jsonPath("$.data.myComments[0].feed.feedId").value(userCommentInfo.feed.feed.feedId))
            .andExpect(jsonPath("$.data.myComments[0].feed.mainDetailFileUrl").value(userCommentInfo.feed.feedDetails[0].media.url))
            .andExpect(
                jsonPath("$.data.myComments[0].feed.type").value(
                    userCommentInfo.feed.feedDetails[0].media.type.value().lowercase(),
                ),
            )
    }
}
