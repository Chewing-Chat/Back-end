package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory.createFeed
import org.chewing.v1.TestDataFactory.createValidJpegMockFile
import org.chewing.v1.controller.feed.FeedController
import org.chewing.v1.dto.request.feed.FeedRequest
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.facade.FriendFeedFacade
import org.chewing.v1.model.feed.FeedId
import org.chewing.v1.model.feed.FeedType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.feed.FeedService
import org.chewing.v1.util.converter.StringToFeedTypeConverter
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class FeedControllerTest : RestDocsTest() {
    private lateinit var feedService: FeedService
    private lateinit var friendFeedFacade: FriendFeedFacade
    private lateinit var feedController: FeedController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver
    private lateinit var feedTypeConverter: StringToFeedTypeConverter

    @BeforeEach
    fun setUp() {
        feedService = mockk()
        friendFeedFacade = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        feedTypeConverter = StringToFeedTypeConverter()
        feedController = FeedController(feedService, friendFeedFacade)
        mockMvc = mockControllerWithAdviceAndCustomConverter(feedController, exceptionHandler, feedTypeConverter, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("내 피드 가져오기 - 썸네일만")
    fun getOwnedFeedThumbnails() {
        val userId = "testUserId"
        val feedId = FeedId.of("testFeedId")
        val feeds = listOf(
            createFeed(feedId, FeedType.FILE),
            createFeed(feedId, FeedType.TEXT_SKY),
            createFeed(feedId, FeedType.TEXT_BLUE),
        )
        every { feedService.getFeeds(UserId.of(userId), UserId.of(userId)) } returns feeds

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/feed/owned/list")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                feeds.forEachIndexed { index, feed ->
                    val formattedUploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    body("data.feeds[$index].feedId", equalTo(feed.feed.feedId.id))
                    body("data.feeds[$index].feedType", equalTo(feed.feed.type.name.lowercase()))
                    body("data.feeds[$index].uploadAt", equalTo(formattedUploadTime))
                    body("data.feeds[$index].ownerId", equalTo(feed.feed.userId.id))
                    when (feed.feed.type) {
                        FeedType.FILE -> {
                            body("data.feeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                            body("data.feeds[$index].count", equalTo(feed.feedDetails.size))
                        }
                        FeedType.TEXT_BLUE -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                        FeedType.TEXT_SKY -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                    }
                    when (feed.feed.type) {
                        FeedType.FILE -> {
                            body("data.feeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                            body("data.feeds[$index].count", equalTo(feed.feedDetails.size))
                        }
                        FeedType.TEXT_BLUE -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                        FeedType.TEXT_SKY -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                    }
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
                        fieldWithPath("data.feeds[].uploadAt").description("피드 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                        fieldWithPath("data.feeds[].feedType").description("피드 타입(TEXT_BLUE, TEXT_SKY, FILE)"),

                        // FILE 타입에 대한 설명
                        fieldWithPath("data.feeds[].thumbnailFileUrl").optional().description("썸네일 파일 URL (파일 타입일 경우)"),
                        fieldWithPath("data.feeds[].fileType").optional().description("미디어 타입(image/png, image/jpeg, image/jpg, image/png) (파일 타입일 경우)"),
                        fieldWithPath("data.feeds[].count").optional().description("파일 개수 (파일 타입일 경우)"),
                        // TEXT 타입에 대한 설명
                        fieldWithPath("data.feeds[].content").optional().description("텍스트 피드 내용 (TEXT_BLUE, TEXT_SKY 타입일 경우)"),
                        fieldWithPath("data.feeds[].ownerId").description("피드 소유자 ID"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("친구 피드 가져오기 - 썸네일만")
    fun getFriendFeedThumbnails() {
        val userId = "testUserId"
        val friendId = "testFriendId"
        val feedId = FeedId.of("testFeedId")
        val feeds = listOf(
            createFeed(feedId, FeedType.FILE),
            createFeed(feedId, FeedType.TEXT_SKY),
            createFeed(feedId, FeedType.TEXT_BLUE),
        )
        every { friendFeedFacade.getFriendFeeds(UserId.of(userId), UserId.of(friendId)) } returns feeds

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/feed/friend/{friendId}/list", friendId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                feeds.forEachIndexed { index, feed ->
                    val formattedUploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                    body("data.feeds[$index].feedId", equalTo(feed.feed.feedId.id))
                    body("data.feeds[$index].feedType", equalTo(feed.feed.type.name.lowercase()))
                    body("data.feeds[$index].uploadAt", equalTo(formattedUploadTime))
                    body("data.feeds[$index].ownerId", equalTo(feed.feed.userId.id))
                    when (feed.feed.type) {
                        FeedType.FILE -> {
                            body("data.feeds[$index].thumbnailFileUrl", equalTo(feed.feedDetails[0].media.url))
                            body("data.feeds[$index].count", equalTo(feed.feedDetails.size))
                        }
                        FeedType.TEXT_BLUE -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                        FeedType.TEXT_SKY -> {
                            body("data.feeds[$index].content", equalTo(feed.feed.content))
                        }
                    }
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
                        fieldWithPath("data.feeds[].uploadAt").description("피드 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                        fieldWithPath("data.feeds[].feedType").description("피드 타입(TEXT_BLUE, TEXT_SKY, FILE)"),

                        // FILE 타입에 대한 설명
                        fieldWithPath("data.feeds[].thumbnailFileUrl").optional().description("썸네일 파일 URL (파일 타입일 경우)"),
                        fieldWithPath("data.feeds[].fileType").optional().description("미디어 타입(image/png, image/jpeg, image/jpg, image/png) (파일 타입일 경우)"),
                        fieldWithPath("data.feeds[].count").optional().description("파일 개수 (파일 타입일 경우)"),

                        // TEXT 타입에 대한 설명
                        fieldWithPath("data.feeds[].content").optional().description("텍스트 피드 내용 (TEXT_BLUE, TEXT_SKY 타입일 경우)"),

                        fieldWithPath("data.feeds[].ownerId").description("피드 소유자 ID"),
                    ),
                ),
            )
    }

    @Test
    @DisplayName("피드 상세 가져오기")
    fun getFeed() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        val feedId = FeedId.of("testFeedId")
        val feed = createFeed(feedId, FeedType.FILE)
        val uploadTime = feed.feed.uploadAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        every { feedService.getFeed(any(), UserId.of(userId)) } returns feed

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/feed/{feedId}/detail", testFeedId)
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("status", equalTo(200))
            .apply {
                body("data.feedId", equalTo(feed.feed.feedId.id))
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
                        fieldWithPath("data.uploadTime").description("피드 업로드 시간 - 형식 yyyy-MM-dd HH:mm:ss"),
                        fieldWithPath("data.details[].index").description("미디어 인덱스(0부터 시작)"),
                        fieldWithPath("data.details[].fileUrl").description("미디어 파일 URL"),
                        fieldWithPath("data.details[].type").description("미디어 타입(image/png, image/jpeg, image/jpg, image/png)"),
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    fun getFeedFailedNotVisible() {
        val testFeedId = "testFeedId"
        val userId = "testUserId"
        every { feedService.getFeed(any(), UserId.of(userId)) } throws ConflictException(ErrorCode.FEED_IS_NOT_VISIBLE)

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/feed/{feedId}/detail", testFeedId)
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FEED_IS_NOT_VISIBLE,
                        "피드의 접근 권한이 없습니다.",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("피드 삭제")
    fun deleteFeeds() {
        val requestBody = listOf(
            FeedRequest.Delete(
                feedId = "testFeedId",
            ),
            FeedRequest.Delete(
                feedId = "testFeedId2",
            ),
        )
        every { feedService.removes(any(), requestBody.map { it.toFeedId() }) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/feed")
            .then()
            .assertCommonSuccessResponse()
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
    }

    @Test
    fun deleteFeedFailedNotOwner() {
        val requestBody = listOf(
            FeedRequest.Delete(
                feedId = "testFeedId",
            ),
            FeedRequest.Delete(
                feedId = "testFeedId2",
            ),
        )
        every { feedService.removes(any(), requestBody.map { it.toFeedId() }) } throws ConflictException(ErrorCode.FEED_IS_NOT_OWNED)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/feed")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FEED_IS_NOT_OWNED,
                        "피드의 소유자가 아닙니다.",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("피드 파일 추가")
    fun addFileFeed() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")
        val feedId = FeedId.of("testFeedId1")
        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        every { feedService.makeFile(any(), any(), any(), any(), any(), any()) } returns feedId

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed/file")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("status", equalTo(201))
            .body("data.feedId", equalTo(feedId.id))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestParts(
                        partWithName("files").description("피드에 추가할 이미지 파일 (image/jpeg)")
                            .description("피드에 추가할 이미지 파일 (image/jpeg) - 형식은 0.jpg, 1.jpg, ..."),
                    ),
                    queryParameters(
                        parameterWithName("content").description("피드 내용"),
                        parameterWithName("friendIds").description("피드를 공유할 친구 ID 목록(POST /api/feed?content=testContent&friendIds=testFriendId&friendIds=testFriendId2) 형식"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feedId").description("피드 아이디"),
                    ),
                ),
            )
    }

    @Test
    fun addFileFeedFailedFileNameCouldNotBeEmpty() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        every { feedService.makeFile(any(), any(), any(), any(), any(), any()) } throws ConflictException(ErrorCode.FILE_NAME_COULD_NOT_EMPTY)

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .queryParam("type", FeedType.FILE.name.lowercase())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed")
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
    fun addFileFeedFailedFileNameIncorrect() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        every { feedService.makeFile(any(), any(), any(), any(), any(), any()) } throws ConflictException(ErrorCode.FILE_NAME_INCORRECT)

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .queryParam("type", FeedType.FILE.name.lowercase())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FILE_NAME_INCORRECT,
                        "파일 이름이 올바르지 않습니다.",
                    ),
                ),
            )
    }

    @Test
    fun addFileFeedFailedFileNotSupportFileType() {
        val mockFile = MockMultipartFile(
            "file",
            "testFile.exe",
            "application/octet-stream",
            "Test content".toByteArray(),
        )

        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .queryParam("type", FeedType.FILE.name.lowercase())
            .multiPart("files", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .multiPart("files", mockFile.originalFilename, mockFile.bytes, mockFile.contentType)
            .post("/api/feed")
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
    fun addFileFeedFailedFileConvertFailed() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        every { feedService.makeFile(any(), any(), any(), any(), any(), any()) } throws ConflictException(ErrorCode.FILE_CONVERT_FAILED)

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .queryParam("type", FeedType.FILE.name.lowercase())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed")
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
    fun addFileFeedFailedFileUploadFailed() {
        val mockFile1 = createValidJpegMockFile("0.jpg")
        val mockFile2 = createValidJpegMockFile("1.jpg")

        val testFriendIds = listOf<String>("testFriendId", "testFriendId2")

        every { feedService.makeFile(any(), any(), any(), any(), any(), any()) } throws ConflictException(ErrorCode.FILE_UPLOAD_FAILED)

        given()
            .setupAuthenticatedMultipartRequest()
            .queryParam("content", "testContent")
            .queryParam("friendIds", *testFriendIds.toTypedArray())
            .queryParam("type", FeedType.FILE.name.lowercase())
            .multiPart("files", mockFile1.originalFilename, mockFile1.bytes, MediaType.IMAGE_JPEG_VALUE)
            .multiPart("files", mockFile2.originalFilename, mockFile2.bytes, MediaType.IMAGE_JPEG_VALUE)
            .post("/api/feed")
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
    fun addTextFeed() {
        val requestBody = FeedRequest.CreateText(
            content = "testContent",
            type = FeedType.TEXT_BLUE.name.lowercase(),
            friendIds = listOf("testFriendId", "testFriendId2"),
        )
        val feedId = FeedId.of("testFeedId1")

        every { feedService.makeText(any(), any(), any(), any()) } returns feedId

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .post("/api/feed/text")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .body("status", equalTo(201))
            .body("data.feedId", equalTo(feedId.id))
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    requestFields(
                        fieldWithPath("content").description("피드 내용"),
                        fieldWithPath("type").description("피드 타입(TEXT_BLUE, TEXT_SKY)"),
                        fieldWithPath("friendIds").description("피드를 공유할 친구 ID 목록"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.feedId").description("피드 아이디"),
                    ),
                ),
            )
    }
}
