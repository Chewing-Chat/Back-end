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
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.friend.FriendController
import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.service.friend.FriendShipService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
class FriendControllerTest : RestDocsTest() {
    private lateinit var friendFacade: FriendFacade
    private lateinit var friendShipService: FriendShipService
    private lateinit var friendController: FriendController
    private lateinit var exceptionHandler: GlobalExceptionHandler

    @BeforeEach
    fun setUp() {
        friendFacade = mockk()
        friendShipService = mockk()
        exceptionHandler = GlobalExceptionHandler()
        friendController = FriendController(friendFacade, friendShipService)
        mockMvc = mockController(friendController, exceptionHandler)
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
                TestDataFactory.createFriend("testFriendId1"),
                TestDataFactory.createFriend("testFriendId2"),
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
                        fieldWithPath("data.friends[].status").description("차단 여부"),
                        fieldWithPath("data.friends[].friendId").description("친구 ID"),
                        fieldWithPath("data.friends[].profileImageUrl").description("프로필 이미지 URL"),
                        fieldWithPath("data.friends[].profileImageType").description("프로필 이미지 타입(image/jpeg, image/png)"),
                        fieldWithPath("data.friends[].statusMessage").description("상태 메시지"),
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
            .assertCommonSuccessResponse()
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
}
