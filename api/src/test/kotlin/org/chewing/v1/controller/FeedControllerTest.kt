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
import org.chewing.v1.TestDataFactory.createFeed
import org.chewing.v1.controller.feed.FeedController
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class FeedControllerTest : RestDocsTest() {
    private lateinit var feedService: FeedService
    private lateinit var feedController: FeedController
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        feedService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        feedController = FeedController(feedService)
        mockMvc = mockController(feedController, exceptionHandler)
    }

    @Test
    @DisplayName("내 피드 가져오기 - 썸네일만")
    fun getOwnedFeedThumbnails() {
        val userId = "testUserId"
        val feeds = listOf(createFeed())
        every { feedService.getFeeds(userId, userId) } returns feeds

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer accessToken")
            .get("/api/feed/owned/list")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                feeds.forEachIndexed { index, feed ->
                    body("data.feeds[$index].feedId", equalTo(feed.feed.feedId))
                    body("data.feeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                    body("data.feeds[$index].type", equalTo(feed.feedDetails[0].media.type.value().lowercase()))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feeds[].feedId").description("피드 아이디"),
                        fieldWithPath("data.feeds[].thumbnailFileUrl").description("썸네일 파일 URL(피드 첫 번째 이미지)"),
                        fieldWithPath("data.feeds[].type").description("미디어 타입(image/png, image/jpeg, image/jpg, image/png)"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("친구 피드 가져오기 - 썸네일만")
    fun getFriendFeedThumbnails() {
        val userId = "testUserId"
        val friendId = "testFriendId"
        val feeds = listOf(createFeed())
        every { feedService.getFeeds(userId, friendId) } returns feeds

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer accessToken")
            .get("/api/feed/friend/{friendId}/list", friendId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                feeds.forEachIndexed { index, feed ->
                    body("data.feeds[$index].feedId", equalTo(feed.feed.feedId))
                    body("data.feeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                    body("data.feeds[$index].type", equalTo(feed.feedDetails[0].media.type.value().lowercase()))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("friendId").description("조회할 친구의 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feeds[].feedId").description("피드 아이디"),
                        fieldWithPath("data.feeds[].thumbnailFileUrl").description("썸네일 파일 URL"),
                        fieldWithPath("data.feeds[].type").description("미디어 타입(image/png, image/jpeg, image/jpg, image/png)"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("피드 상세 가져오기")
    fun getFeed() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        val feed = createFeed()
        val uploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss"))
        every { feedService.getFeed(testFeedId, userId) } returns feed

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer accessToken")
            .get("/api/feed/{feedId}/detail", testFeedId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                body("data.feedId", equalTo(feed.feed.feedId))
                body("data.content", equalTo(feed.feed.content))
                body("data.uploadTime", equalTo(uploadTime))
                feed.feedDetails.forEachIndexed { index, feedDetail ->
                    body("data.details[$index].index", equalTo(feedDetail.media.index))
                    body("data.details[$index].fileUrl", equalTo(feedDetail.media.url))
                    body("data.details[$index].type", equalTo(feedDetail.media.type.value().lowercase()))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    pathParameters(
                        parameterWithName("feedId").description("조회할 피드의 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feedId").description("피드 아이디"),
                        fieldWithPath("data.content").description("피드 내용"),
                        fieldWithPath("data.uploadTime").description("피드 업로드 시간 - 형식 yy-MM-dd HH:mm:ss"),
                        fieldWithPath("data.details[].index").description("미디어 인덱스(0부터 시작)"),
                        fieldWithPath("data.details[].fileUrl").description("미디어 파일 URL"),
                        fieldWithPath("data.details[].type").description("미디어 타입(image/png, image/jpeg, image/jpg, image/png)"),
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    @DisplayName("피드 삭제")
    fun deleteFeeds() {
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

        val result = given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", userId)
            .header("Authorization", "Bearer accessToken")
            .body(requestBody)
            .delete("/api/feed")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("[].feedId").description("삭제할 피드 아이디"),
                    ),
                    responseSuccessFields(),
                ),
            )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("피드 추가")
    fun addFeed() {
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

        given()
            .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
            .attribute("userId", "testUserId")
            .header("Authorization", "Bearer accessToken")
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("status", equalTo(201))
            .body("data.feedId", equalTo(feedId))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestParts(
                        partWithName("files").description("피드에 추가할 이미지 파일 (image/jpeg)").description("피드에 추가할 이미지 파일 (image/jpeg) - 형식은 0.jpg, 1.jpg, ..."),
                    ),
                    queryParameters(
                        parameterWithName("content").description("피드 내용"),
                        parameterWithName("friendIds").description("피드를 공유할 친구 ID 목록"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feedId").description("피드 아이디"),
                    ),
                ),
            )
    }
}
