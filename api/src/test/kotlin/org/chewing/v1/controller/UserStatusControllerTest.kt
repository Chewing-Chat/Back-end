package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.user.UserStatusController
import org.chewing.v1.dto.request.user.UserStatusRequest
import org.chewing.v1.service.user.UserStatusService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ActiveProfiles("test")
class UserStatusControllerTest : RestDocsTest() {

    private lateinit var userStatusService: UserStatusService
    private lateinit var userStatusController: UserStatusController

    @BeforeEach
    fun setUp() {
        userStatusService = mockk()
        userStatusController = UserStatusController(userStatusService)
        mockMvc = mockController(userStatusController)
    }

    @Test
    @DisplayName("사용자 상태 선택 해제")
    fun deleteProfileSelectedStatus() {
        every { userStatusService.deleteSelectUserStatus(any()) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/user/status/select")
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 상태 삭제")
    fun deleteProfileStatus() {
        val requestBody = listOf(
            UserStatusRequest.Delete(statusId = "testStatusId"),
            UserStatusRequest.Delete(statusId = "testStatusId2"),
        )

        every { userStatusService.deleteUserStatuses(any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/user/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 상태 선택")
    fun changeProfileSelectedStatus() {
        val requestBody = UserStatusRequest.Update(
            statusId = "testStatusId",
        )

        every { userStatusService.selectUserStatus(any(), any()) } just Runs

        val result = mockMvc.perform(
            MockMvcRequestBuilders.put("/api/user/status/select")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessResponse(result)
    }

    @Test
    @DisplayName("사용자 상태 추가")
    fun addProfileStatus() {
        val requestBody = UserStatusRequest.Add(
            emoji = "testEmoji",
            message = "testMessage",
        )
        every { userStatusService.createUserStatus(any(), any(), any()) } just Runs
        val result = mockMvc.perform(
            MockMvcRequestBuilders.post("/api/user/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
        performCommonSuccessCreateResponse(result)
    }

    @Test
    @DisplayName("사용자 상태 조회")
    fun getUserStatus() {
        val statuses = listOf(TestDataFactory.createUserStatus())

        every { userStatusService.getUserStatuses(any()) } returns statuses
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/user/status")
                .requestAttr("userId", "testUserId"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(200))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.statuses[0].statusId").value(statuses[0].statusId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.statuses[0].message").value(statuses[0].message))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.statuses[0].emoji").value(statuses[0].emoji))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.statuses[0].selected").value(statuses[0].isSelected))
    }
}
