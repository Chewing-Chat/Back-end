package org.chewing.v1.controller

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory
import org.chewing.v1.controller.user.UserScheduleController
import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class UserScheduleControllerTest : RestDocsTest() {

    private lateinit var scheduleService: ScheduleService
    private lateinit var userScheduleController: UserScheduleController

    @BeforeEach
    fun setUp() {
        scheduleService = mockk()
        userScheduleController = UserScheduleController(scheduleService)
        mockMvc = mockController(userScheduleController, GlobalExceptionHandler())
    }

    @Test
    fun getSchedule() {
        val scheduleInfo = TestDataFactory.createScheduleInfo(ScheduleStatus.ACTIVE)
        val scheduleParticipants = listOf(
            TestDataFactory.createScheduleParticipant(ScheduleParticipantStatus.ACTIVE),
            TestDataFactory.createScheduleParticipant(ScheduleParticipantStatus.ACTIVE),
        )
        val schedule = TestDataFactory.createSchedule(scheduleInfo, scheduleParticipants)
        val schedules = listOf(schedule)
        val year = 2021
        val month = 1
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        // When
        every { scheduleService.fetches(any(), any()) } returns schedules

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "testUserId")
            .queryParam("year", year.toString())
            .queryParam("month", month.toString())
            .header("Authorization", "Bearer accessToken")
            .get("/api/schedule")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply {
                schedules.forEachIndexed { index, schedule ->
                    body("data.schedules[$index].scheduleId", equalTo(schedules[index].info.id))
                    body("data.schedules[$index].title", equalTo(schedules[index].info.content.title))
                    body("data.schedules[$index].memo", equalTo(schedules[index].info.content.memo))
                    body(
                        "data.schedules[$index].dateTime",
                        equalTo(schedules[index].info.time.dateTime.format(formatter)),
                    )
                    body("data.schedules[$index].location", equalTo(schedules[index].info.content.location))
                    body("data.schedules[$index].timeDecided", equalTo(schedules[index].info.time.timeDecided))
                    schedules[index].participants.forEachIndexed { participantIndex, participant ->
                        body(
                            "data.schedules[$index].participants[$participantIndex].friendId",
                            equalTo(participant.userId),
                        )
                    }
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    queryParameters(
                        parameterWithName("year").description("년도(yyyy) -> 2021"),
                        parameterWithName("month").description("월(1 ~ 12)"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.schedules[].scheduleId").description("일정 ID"),
                        fieldWithPath("data.schedules[].title").description("일정 제목"),
                        fieldWithPath("data.schedules[].dateTime").description("일정 시간"),
                        fieldWithPath("data.schedules[].memo").description("일정 메모"),
                        fieldWithPath("data.schedules[].location").description("일정 장소"),
                        fieldWithPath("data.schedules[].timeDecided").description("시간 확정 여부"),
                        fieldWithPath("data.schedules[].participants[].friendId").description("참여자(친구) ID, : 이떄 자기 자신의 아이디는 빠짐"),
                    ),
                ),
            )
    }

    @Test
    fun deleteSchedule() {
        val scheduleId = "testScheduleId"
        val requestBody = ScheduleRequest.Delete(
            scheduleId = scheduleId,
        )
        // When
        every { scheduleService.delete(any(), any()) } just runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "testUserId")
            .body(requestBody)
            .header("Authorization", "Bearer accessToken")
            .delete("/api/schedule")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("scheduleId").description("일정 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun createSchedule() {
        val requestBody = ScheduleRequest.Create(
            title = "testTitle",
            dateTime = "2021-01-01 00:00:00",
            memo = "testMemo",
            location = "testLocation",
            timeDecided = true,
            friendIds = listOf("testFriendId1", "testFriendId2"),
        )
        val scheduleId = "testScheduleId"
        every { scheduleService.create(any(), any(), any(), any()) } returns scheduleId

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "testUserId")
            .body(requestBody)
            .header("Authorization", "Bearer accessToken")
            .post("/api/schedule")
            .then()
            .statusCode(HttpStatus.CREATED.value())
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("title").description("일정 제목"),
                        fieldWithPath("dateTime").description("일정 시간"),
                        fieldWithPath("memo").description("일정 메모"),
                        fieldWithPath("location").description("일정 장소"),
                        fieldWithPath("timeDecided").description("시간 확정 여부(true/false) - 디자인 상의 미정을 의미"),
                        fieldWithPath("friendIds[]").description("참여자(친구) ID 목록 (각 ID는 문자열)"),
                    ),
                    requestAccessTokenFields(),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.scheduleId").description("생성된 일정 ID"),
                    ),
                ),
            )
    }

    @Test
    fun updateSchedule() {
        val requestBody = ScheduleRequest.Update(
            scheduleId = "testScheduleId",
            title = "testTitle",
            dateTime = "2021-01-01 00:00:00",
            memo = "testMemo",
            location = "testLocation",
            timeDecided = true,
            friendIds = listOf("testFriendId1", "testFriendId2"),
        )
        every { scheduleService.update(any(), any(), any(), any(), any()) } just runs

        given()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .attribute("userId", "testUserId")
            .body(requestBody)
            .header("Authorization", "Bearer accessToken")
            .put("/api/schedule")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("scheduleId").description("일정 ID"),
                        fieldWithPath("title").description("일정 제목"),
                        fieldWithPath("dateTime").description("일정 시간"),
                        fieldWithPath("memo").description("일정 메모"),
                        fieldWithPath("location").description("일정 장소"),
                        fieldWithPath("timeDecided").description("시간 확정 여부(true/false) - 디자인 상의 미정을 의미"),
                        fieldWithPath("friendIds[]").description("참여자(친구) ID 목록 (각 ID는 문자열)"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }
}
