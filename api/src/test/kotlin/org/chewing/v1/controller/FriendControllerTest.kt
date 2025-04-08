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
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.friend.FriendController
import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.model.friend.FriendShipStatus
import org.chewing.v1.model.user.UserId
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.service.user.UserService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.chewing.v1.util.security.UserArgumentResolver
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class FriendControllerTest : RestDocsTest() {
    private lateinit var friendFacade: FriendFacade
    private lateinit var friendShipService: FriendShipService
    private lateinit var friendController: FriendController
    private lateinit var exceptionHandler: GlobalExceptionHandler
    private lateinit var userArgumentResolver: UserArgumentResolver
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        friendFacade = mockk()
        friendShipService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        userArgumentResolver = UserArgumentResolver()
        friendController = FriendController(friendFacade, friendShipService)
        mockMvc = mockController(friendController, exceptionHandler, userArgumentResolver)
        val userId = UserId.of("testUserId")
        val authentication = UsernamePasswordAuthenticationToken(userId, null)
        SecurityContextHolder.getContext().authentication = authentication
    }

    @Test
    @DisplayName("전화번호로 친구 추가")
    fun createFriends() {
        val requestBody1 = FriendRequest.Create(
            phoneNumber = "01012345678",
            countryCode = "82",
            name = "testName",
        )

        val requestBody2 = FriendRequest.Create(
            phoneNumber = "01012345678",
            countryCode = "82",
            name = "testName",
        )

        val friends =
            listOf(
                TestDataFactory.createFriend("testFriendId1", FriendShipStatus.FRIEND),
                TestDataFactory.createFriend("testFriendId2", FriendShipStatus.DELETE),
                TestDataFactory.createFriend("testFriendId3", FriendShipStatus.BLOCK),
                TestDataFactory.createFriend("testFriendId4", FriendShipStatus.FRIEND),
            )

        every { friendFacade.createFriends(any(), any()) } returns friends

        given()
            .setupAuthenticatedJsonRequest()
            .body(listOf(requestBody1, requestBody2))
            .post("/api/friend/list")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .apply {
                friends.forEachIndexed { index, s ->
                    body("data.friends[$index].phoneNumber", equalTo(s.user.localPhoneNumber.number))
                    body("data.friends[$index].countryCode", equalTo(s.user.localPhoneNumber.countryCode))
                    body("data.friends[$index].name", equalTo(s.name))
                    body("data.friends[$index].favorite", equalTo(s.isFavorite))
                    body("data.friends[$index].status", equalTo(s.status.name.lowercase()))
                    body("data.friends[$index].friendId", equalTo(s.user.info.userId.id))
                    body("data.friends[$index].profileImageUrl", equalTo(s.user.info.image.url))
                    body("data.friends[$index].profileImageType", equalTo(s.user.info.image.type.value()))
                    body("data.friends[$index].statusMessage", equalTo(s.user.info.statusMessage))
                    body("data.friends[$index].birthday", equalTo(s.user.info.birthday.toString()))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("[].phoneNumber").description("전화번호"),
                        fieldWithPath("[].countryCode").description("국가 코드"),
                        fieldWithPath("[].name").description("친구 이름"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.friends[].phoneNumber").description("전화번호"),
                        fieldWithPath("data.friends[].countryCode").description("국가 코드"),
                        fieldWithPath("data.friends[].name").description("친구 이름"),
                        fieldWithPath("data.friends[].favorite").description("즐겨찾기 여부"),
                        fieldWithPath("data.friends[].status").description("차단 여부/삭제 여부/NORMAL 시 친구가 아닌 상태(전화번호 추가 필요)"),
                        fieldWithPath("data.friends[].friendId").description("친구 ID"),
                        fieldWithPath("data.friends[].profileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.friends[].profileImageType").description("프로필 이미지 타입(image/jpeg, image/png)"),
                        fieldWithPath("data.friends[].statusMessage").description("상태 메시지"),
                        fieldWithPath("data.friends[].birthday").description("생일(yyyy-MM-dd), 없다면 빈칸"),
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    @DisplayName("즐겨찾기 변경")
    fun changeFavorite() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/favorite")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                        fieldWithPath("favorite").description("즐겨찾기 여부"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun changeFavoriteFailedDelete() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_DELETED)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/favorite")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_DELETED,
                        "친구를 삭제한 상태",
                    ),
                ),
            )
    }

    @Test
    fun changeFavoriteFailedBlocked() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCKED)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/favorite")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCKED,
                        "내가 차단된 상태",
                    ),
                ),
            )
    }

    @Test
    fun changeFavoriteFailedBlock() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCK)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/favorite")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCK,
                        "친구를 차단한 상태",
                    ),
                ),
            )
    }

    @Test
    fun changeFavoriteFailedNormal() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCK)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/favorite")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCK,
                        "친구가 아닌 상태(전화번호 추가 필요)",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("친구 차단")
    fun blockFriend() {
        val requestBody = FriendRequest.Block(
            friendId = "testFriendId",
        )

        every { friendShipService.blockFriendShip(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/friend/block")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun unblockFriend() {
        val requestBody = FriendRequest.Unblock(
            friendId = "testFriendId",
        )

        every { friendShipService.unblockFriendShip(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/unblock")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    @DisplayName("친구 삭제")
    fun deleteFriend() {
        val requestBody = FriendRequest.Delete(
            friendId = "testFriendId",
        )

        every { friendShipService.removeFriendShip(any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/friend")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    @DisplayName("친구 이름 변경")
    fun updateFriendName() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            name = "testName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/name")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                        fieldWithPath("name").description("친구 이름"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun updateFriendNameFailedDelete() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            name = "testName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_DELETED)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/name")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_DELETED,
                        "친구를 삭제한 상태",
                    ),
                ),
            )
    }

    @Test
    fun updateFriendNameFailedBlocked() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            name = "testName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCKED)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/name")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCKED,
                        "내가 차단된 상태",
                    ),
                ),
            )
    }

    @Test
    fun updateFriendNameFailedBlock() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            name = "testName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCK)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/name")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCK,
                        "친구를 차단한 상태",
                    ),
                ),
            )
    }

    @Test
    fun updateFriendNameFailedNormal() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            name = "testName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } throws ConflictException(ErrorCode.FRIEND_BLOCK)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/name")
            .then()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.FRIEND_BLOCK,
                        "친구가 아닌 상태(전화번호 추가 필요)",
                    ),
                ),
            )
    }

    @Test
    @DisplayName("친구 상태 변경")
    fun changeFriendStatus() {
        val requestBody = FriendRequest.UpdateStatus(
            friendId = "testFriendId",
            friendName = "testName",
        )

        every { friendFacade.allowedFriend(any(), any(), any()) } just Runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/friend/allowed")
            .then()
            .assertCommonSuccessResponse()
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("friendId").description("친구 ID"),
                        fieldWithPath("friendName").description("친구 이름"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }
}
