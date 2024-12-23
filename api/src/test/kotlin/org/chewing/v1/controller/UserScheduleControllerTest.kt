package org.chewing.v1.controller

import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.chewing.v1.RestDocsTest
import org.chewing.v1.TestDataFactory.createPrivateSchedule
import org.chewing.v1.TestDataFactory.createPublicSchedule
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
        val schedules = listOf(createPublicSchedule())
        val year = 2021
        val month = 1
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        val startTime = schedules[0].time.startAt.format(formatter)
        val endTime = schedules[0].time.endAt.format(formatter)
        val notificationTime = schedules[0].time.notificationAt.format(formatter)

        // When
        every { scheduleService.fetches(any(), any(), any()) } returns schedules
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
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].startTime").value(startTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].endTime").value(endTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].notificationTime")
                    .value(notificationTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].location").value(schedules[0].content.location),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].private").value(schedules[0].content.private),
            )
    }

    @Test
    fun getFriendSchedule() {
        val schedules = listOf(createPrivateSchedule())
        val year = 2021
        val month = 1
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        val startTime = schedules[0].time.startAt.format(formatter)
        val endTime = schedules[0].time.endAt.format(formatter)
        val notificationTime = schedules[0].time.notificationAt.format(formatter)

        // When
        every { scheduleService.fetches(any(), any(), any()) } returns schedules
        mockMvc.perform(
            MockMvcRequestBuilders.get("/api/schedule/friend/testFriendId")
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
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].startTime").value(startTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].endTime").value(endTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].notificationTime")
                    .value(notificationTime),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].location").value(schedules[0].content.location),
            )
            .andExpect(
                MockMvcResultMatchers.jsonPath("$.data.schedules[0].private").value(schedules[0].content.private),
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
        val requestBody = ScheduleRequest.Add(
            title = "testTitle",
            startTime = "2021-01-01 00:00:00",
            endTime = "2021-01-01 00:00:00",
            notificationTime = "2021-01-01 00:00:00",
            memo = "testMemo",
            location = "testLocation",
            private = false,
        )
        val scheduleId = "testScheduleId"

        every { scheduleService.create(any(), any(), any()) } returns scheduleId

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
