package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.controller.friend.FriendController
import org.chewing.v1.dto.request.friend.FriendRequest
import org.chewing.v1.facade.FriendFacade
import org.chewing.v1.service.friend.FriendShipService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ActiveProfiles("test")
class FriendControllerTest : RestDocsTest() {
    private lateinit var friendFacade: FriendFacade
    private lateinit var friendShipService: FriendShipService
    private lateinit var friendController: FriendController

    @BeforeEach
    fun setUp() {
        friendFacade = mockk()
        friendShipService = mockk()
        friendController = FriendController(friendFacade, friendShipService)
        mockMvc = mockController(friendController)
    }

    @Test
    @DisplayName("이메일로 친구 추가")
    fun addFriendWithEmail() {
        val requestBody = FriendRequest.AddWithEmail(
            email = "test@example.com",
            firstName = "testFirstName",
            lastName = "testLastName",
        )
        every { friendFacade.addFriend(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/friend/email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessCreateResponse(result)
    }

    @Test
    @DisplayName("전화번호로 친구 추가")
    fun addFriendWithPhone() {
        val requestBody = FriendRequest.AddWithPhone(
            phoneNumber = "01012345678",
            countryCode = "82",
            firstName = "testFirstName",
            lastName = "testLastName",
        )

        every { friendFacade.addFriend(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/friend/phone")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessCreateResponse(result)
    }

    @Test
    @DisplayName("즐겨찾기 변경")
    fun changeFavorite() {
        val requestBody = FriendRequest.UpdateFavorite(
            friendId = "testFriendId",
            favorite = true,
        )

        every { friendShipService.changeFriendFavorite(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/friend/favorite")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("친구 차단")
    fun blockFriend() {
        val requestBody = FriendRequest.Block(
            friendId = "testFriendId",
        )

        every { friendShipService.blockFriendShip(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/friend/block")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("친구 삭제")
    fun deleteFriend() {
        val requestBody = FriendRequest.Delete(
            friendId = "testFriendId",
        )

        every { friendShipService.removeFriendShip(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/friend")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("친구 이름 변경")
    fun updateFriendName() {
        val requestBody = FriendRequest.UpdateName(
            friendId = "testFriendId",
            firstName = "testFirstName",
            lastName = "testLastName",
        )

        every { friendShipService.changeFriendName(any(), any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/friend/name")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }
}
