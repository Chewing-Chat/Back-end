package org.chewing.v1.controller

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import org.chewing.v1.RestDocsTest
import org.chewing.v1.RestDocsUtils.requestAccessTokenFields
import org.chewing.v1.RestDocsUtils.requestPreprocessor
import org.chewing.v1.RestDocsUtils.responseErrorFields
import org.chewing.v1.RestDocsUtils.responsePreprocessor
import org.chewing.v1.RestDocsUtils.responseSuccessFields
import org.chewing.v1.TestDataFactory
import org.chewing.v1.schedule.ScheduleController
import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.error.NotFoundException
import org.chewing.v1.model.schedule.ScheduleParticipantRole
import org.chewing.v1.model.schedule.ScheduleParticipantStatus
import org.chewing.v1.model.schedule.ScheduleStatus
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.util.handler.GlobalExceptionHandler
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.test.context.ActiveProfiles
import java.time.format.DateTimeFormatter

@ActiveProfiles("test")
class ScheduleControllerTest : RestDocsTest() {

    private lateinit var scheduleService: ScheduleService
    private lateinit var scheduleController: ScheduleController

    @BeforeEach
    fun setUp() {
        scheduleService = mockk()
        scheduleController = ScheduleController(scheduleService)
        mockMvc = mockController(scheduleController, GlobalExceptionHandler())
    }

    @Test
    fun getSchedules() {
        val scheduleInfo = TestDataFactory.createScheduleInfo(ScheduleStatus.ACTIVE)
        val scheduleParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                ScheduleParticipantStatus.ACTIVE,
                ScheduleParticipantRole.PARTICIPANT,
            ),
            TestDataFactory.createScheduleParticipant(
                ScheduleParticipantStatus.ACTIVE,
                ScheduleParticipantRole.PARTICIPANT,
            ),
        )
        val schedule = TestDataFactory.createSchedule(scheduleInfo, scheduleParticipants)
        val schedules = listOf(schedule)
        val year = 2021
        val month = 1
        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        // When
        every { scheduleService.fetches(any(), any()) } returns schedules

        given()
            .setupAuthenticatedJsonRequest()
            .queryParam("year", year.toString())
            .queryParam("month", month.toString())
            .get("/api/schedule/list")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply {
                schedules.forEachIndexed { index, schedule ->
                    body("data.schedules[$index].scheduleId", equalTo(schedules[index].info.scheduleId.id))
                    body("data.schedules[$index].title", equalTo(schedules[index].info.content.title))
                    body("data.schedules[$index].memo", equalTo(schedules[index].info.content.memo))
                    body(
                        "data.schedules[$index].dateTime",
                        equalTo(schedules[index].info.time.dateTime.format(formatter)),
                    )
                    body("data.schedules[$index].location", equalTo(schedules[index].info.content.location))
                    body("data.schedules[$index].timeDecided", equalTo(schedules[index].info.time.timeDecided))
                    body("data.schedules[$index].isOwned", equalTo(schedules[index].isOwned))
                    body("data.schedules[$index].isParticipant", equalTo(schedules[index].isParticipant))
                    schedules[index].participants.forEachIndexed { participantIndex, participant ->
                        body(
                            "data.schedules[$index].participants[$participantIndex].friendId",
                            equalTo(participant.userId.id),
                        )
                        body(
                            "data.schedules[$index].participants[$participantIndex].friendRole",
                            equalTo(participant.role.name.lowercase()),
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
                        fieldWithPath("data.schedules[].participants[].friendRole").description("참여자 역할(participant/owner)"),
                        fieldWithPath("data.schedules[].isOwned").description("일정 소유자 여부(true/false) -> true 시 일정 삭제 칸 보임/ false 시 일정 취소 칸 안보임"),
                        fieldWithPath("data.schedules[].isParticipant").description("요청한 사람 참여 여부(true/false) -> 요청한 사람의 Id는 노출되지 않음 친구 ID 만 노출"),
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
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/schedule")
            .then()
            .assertCommonSuccessResponse()
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
    fun deleteScheduleScheduleNotParticipant() {
        val scheduleId = "testScheduleId"
        val requestBody = ScheduleRequest.Delete(
            scheduleId = scheduleId,
        )
        // When
        every { scheduleService.delete(any(), any()) } throws NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/schedule")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.SCHEDULE_NOT_PARTICIPANT)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("scheduleId").description("일정 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SCHEDULE_NOT_PARTICIPANT,
                        "일정 참여자가 아닙니다. -> 일정에 참여한 적이 없는 경우 보안상 발생하는 에러 딱히 처리할 것은 없음",
                    ),
                ),
            )
    }

    @Test
    fun deleteScheduleScheduleIsNotOwner() {
        val scheduleId = "testScheduleId"
        val requestBody = ScheduleRequest.Delete(
            scheduleId = scheduleId,
        )
        // When
        every { scheduleService.delete(any(), any()) } throws ConflictException(ErrorCode.SCHEDULE_NOT_OWNER)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/schedule")
            .then()
            .assertErrorResponse(HttpStatus.CONFLICT, ErrorCode.SCHEDULE_NOT_OWNER)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("scheduleId").description("일정 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseErrorFields(
                        HttpStatus.CONFLICT,
                        ErrorCode.SCHEDULE_NOT_OWNER,
                        "일정 소유자가 아닙니다. -> 일정을 생성한 사람이 아닌 경우 -> 일정을 생성한 사람만 삭제 가능(보안상 발생하는 에러 딱히 처리할 것은 없음)",
                    ),
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
        val scheduleId = TestDataFactory.createScheduleId()
        every { scheduleService.create(any(), any(), any(), any()) } returns scheduleId

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
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
            participated = true,
        )
        every { scheduleService.update(any(), any(), any(), any(), any(), any()) } just runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/schedule")
            .then()
            .assertCommonSuccessResponse()
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
                        fieldWithPath("friendIds[]").description("본인을 제외한 참여자(친구) ID 목록 (각 ID는 문자열)"),
                        fieldWithPath("participated").description("요청자 본인 참여 여부(true/false)"),
                    ),
                    requestAccessTokenFields(),
                    responseSuccessFields(),
                ),
            )
    }

    @Test
    fun updateScheduleNotParticipate() {
        val requestBody = ScheduleRequest.Update(
            scheduleId = "testScheduleId",
            title = "testTitle",
            dateTime = "2021-01-01 00:00:00",
            memo = "testMemo",
            location = "testLocation",
            timeDecided = true,
            friendIds = listOf("testFriendId1", "testFriendId2"),
            participated = true,
        )
        every {
            scheduleService.update(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            )
        } throws NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .put("/api/schedule")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.SCHEDULE_NOT_PARTICIPANT)
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
                        fieldWithPath("friendIds[]").description("참여자(친구) ID 목록 (각 ID는 문자열), 업데이트시에는 기존에 있는 친구아이디를 포함한 모든 참여자 ID를 보내야 함(본인 제외)"),
                        fieldWithPath("participated").description("요청자 본인 참여 여부(true/false)"),
                    ),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SCHEDULE_NOT_PARTICIPANT,
                        "일정 참여자가 아닙니다. -> 일정에 참여한 적이 없는 경우 보안상 발생하는 에러 딱히 처리할 것은 없음",
                    ),
                    requestAccessTokenFields(),
                ),
            )
    }

    @Test
    fun cancelSchedule() {
        val requestBody = ScheduleRequest.Cancel(
            scheduleId = "testScheduleId",
        )
        every { scheduleService.cancel(any(), any()) } just runs

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/schedule/cancel")
            .then()
            .assertCommonSuccessResponse()
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
    fun cancelScheduleNotParticipate() {
        val requestBody = ScheduleRequest.Cancel(
            scheduleId = "testScheduleId",
        )
        every { scheduleService.cancel(any(), any()) } throws NotFoundException(ErrorCode.SCHEDULE_NOT_PARTICIPANT)

        given()
            .setupAuthenticatedJsonRequest()
            .body(requestBody)
            .delete("/api/schedule/cancel")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.SCHEDULE_NOT_PARTICIPANT)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestFields(
                        fieldWithPath("scheduleId").description("일정 ID"),
                    ),
                    requestAccessTokenFields(),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SCHEDULE_NOT_PARTICIPANT,
                        "일정 참여자가 아닙니다. -> 일정에 참여한 적이 없는 경우 보안상 발생하는 에러 딱히 처리할 것은 없음",
                    ),
                ),
            )
    }

    @Test
    fun getScheduleLogs() {
        val scheduleLogs = TestDataFactory.createScheduleLogs()

        every { scheduleService.fetchLogs(any()) } returns scheduleLogs

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/schedule/logs")
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply {
                scheduleLogs.forEachIndexed { index, scheduleLog ->
                    body("data.logs[$index].scheduleId", equalTo(scheduleLogs[index].scheduleId.id))
                    body("data.logs[$index].userId", equalTo(scheduleLogs[index].userId.id))
                    body("data.logs[$index].action", equalTo(scheduleLogs[index].action.name.lowercase()))
                    body("data.logs[$index].createAt", equalTo(scheduleLogs[index].createAt))
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
                        fieldWithPath("data.logs[].scheduleId").description("일정 ID"),
                        fieldWithPath("data.logs[].userId").description("로그를 남긴 사용자 ID"),
                        fieldWithPath("data.logs[].action").description("로그 액션(일정 생성, 일정 수정, 일정 삭제, 일정 취소)"),
                        fieldWithPath("data.logs[].createAt").description("로그 생성 시간"),
                    ),
                ),
            )
    }

    @Test
    fun getSchedule() {
        val scheduleInfo = TestDataFactory.createScheduleInfo(ScheduleStatus.ACTIVE)
        val scheduleParticipants = listOf(
            TestDataFactory.createScheduleParticipant(
                ScheduleParticipantStatus.ACTIVE,
                ScheduleParticipantRole.PARTICIPANT,
            ),
            TestDataFactory.createScheduleParticipant(
                ScheduleParticipantStatus.ACTIVE,
                ScheduleParticipantRole.PARTICIPANT,
            ),
        )

        val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
        val schedule = TestDataFactory.createSchedule(scheduleInfo, scheduleParticipants)

        every { scheduleService.fetch(any(), any()) } returns schedule

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/schedule/{scheduleId}", schedule.info.scheduleId.id)
            .then()
            .statusCode(HttpStatus.OK.value())
            .apply {
                body("data.scheduleId", equalTo(schedule.info.scheduleId.id))
                body("data.title", equalTo(schedule.info.content.title))
                body("data.dateTime", equalTo(schedule.info.time.dateTime.format(formatter)))
                body("data.memo", equalTo(schedule.info.content.memo))
                body("data.location", equalTo(schedule.info.content.location))
                body("data.timeDecided", equalTo(schedule.info.time.timeDecided))
                body("data.isOwned", equalTo(schedule.isOwned))
                body("data.isParticipant", equalTo(schedule.isParticipant))
                schedule.participants.forEachIndexed { index, participant ->
                    body("data.participants[$index].friendId", equalTo(participant.userId.id))
                    body("data.participants[$index].friendRole", equalTo(participant.role.name.lowercase()))
                }
            }
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("scheduleId").description("일정 ID"),
                    ),
                    responseFields(
                        fieldWithPath("status").description("상태 코드"),
                        fieldWithPath("data.scheduleId").description("일정 ID"),
                        fieldWithPath("data.title").description("일정 제목"),
                        fieldWithPath("data.dateTime").description("일정 시간"),
                        fieldWithPath("data.memo").description("일정 메모"),
                        fieldWithPath("data.location").description("일정 장소"),
                        fieldWithPath("data.timeDecided").description("시간 확정 여부"),
                        fieldWithPath("data.isOwned").description("일정 소유자 여부(true/false) -> true 시 일정 삭제 칸 보임/ false 시 일정 취소 칸 안보임"),
                        fieldWithPath("data.isParticipant").description("요청한 사람 참여 여부(true/false) -> 요청한 사람의 Id는 노출되지 않음 친구 ID 만 노출"),
                        fieldWithPath("data.participants[].friendId").description("참여자(친구) ID, : 이떄 자기 자신의 아이디는 빠짐"),
                        fieldWithPath("data.participants[].friendRole").description("참여자 역할(participant/owner)"),
                    ),
                ),
            )
    }

    @Test
    fun getScheduleNotFound() {
        every { scheduleService.fetch(any(), any()) } throws NotFoundException(ErrorCode.SCHEDULE_NOT_FOUND)

        given()
            .setupAuthenticatedJsonRequest()
            .get("/api/schedule/{scheduleId}", "inValidScheduleId")
            .then()
            .assertErrorResponse(HttpStatus.NOT_FOUND, ErrorCode.SCHEDULE_NOT_FOUND)
            .apply(
                document(
                    "{class-name}/{method-name}",
                    requestPreprocessor(),
                    responsePreprocessor(),
                    requestAccessTokenFields(),
                    pathParameters(
                        parameterWithName("scheduleId").description("일정 ID"),
                    ),
                    responseErrorFields(
                        HttpStatus.NOT_FOUND,
                        ErrorCode.SCHEDULE_NOT_FOUND,
                        "일정을 찾을 수 없는 경우 - 잘못된 일정 ID를 요청한 경우",
                    ),
                ),
            )
    }
}
