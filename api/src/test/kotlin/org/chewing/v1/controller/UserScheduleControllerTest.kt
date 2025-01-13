package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.user.UserScheduleController
import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.service.user.ScheduleService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class UserScheduleControllerTest : RestDocsTest() {

    private lateinit var scheduleService: ScheduleService
    private lateinit var userScheduleController: UserScheduleController

    @BeforeEach
    fun setUp() {
        scheduleService = mockk()
        userScheduleController = UserScheduleController(scheduleService)
        mockMvc = mockController(userScheduleController)
    }

    @Test
    fun getSchedule() {
        val schedules = listOf(TestDataFactory.createSchedule())
        val year = 2021
        val month = 1
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        val dateTime = schedules[0].time.dateTime.format(formatter)
        // When
        every { scheduleService.fetches(any(), any()) } returns schedules
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .param("year", year.toString())
                .param("month", month.toString())
                .requestAttr("userId", "testUserId"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.schedules[0].scheduleId").value(schedules[0].id))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.schedules[0].title").value(schedules[0].content.title))
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.schedules[0].memo").value(schedules[0].content.memo))
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].dateTime").value(dateTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].location").value(schedules[0].content.location),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].timeDecided")
                    .value(schedules[0].time.timeDecided),
            )
    }

    @Test
    fun deleteSchedule() {
        val requestBody = ScheduleRequest.Delete(
            scheduleId = "testScheduleId",
        )

        every { scheduleService.delete(any()) } just Runs
        // When
        mockMvc.perform(
            MockMvcRequestBuilders.delete("/api/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
    }

    @Test
    fun createSchedule() {
        val requestBody = ScheduleRequest.Create(
            title = "testTitle",
            dateTime = "2021-01-01 00:00:00",
            memo = "testMemo",
            location = "testLocation",
            timeDecided = true,
            friendIds = listOf("testFriendId"),
        )
        val scheduleId = "testScheduleId"

        every { scheduleService.create(any(), any(), any(), any()) } returns scheduleId

        // When
        mockMvc.perform(
            MockMvcRequestBuilders.post("/api/schedule")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonBody(requestBody))
                .requestAttr("userId", "testUserId"),
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("$.data.scheduleId").value(scheduleId))
    }
}
