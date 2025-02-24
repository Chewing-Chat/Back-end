package org.chewing.v1.controller.schedule

import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.dto.response.schedule.ScheduleIdResponse
import org.chewing.v1.dto.response.schedule.ScheduleListResponse
import org.chewing.v1.dto.response.schedule.ScheduleListResponse.ScheduleResponse
import org.chewing.v1.dto.response.schedule.ScheduleLogsResponse
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.security.CurrentUser
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule")
class ScheduleController(
    private val scheduleService: ScheduleService,
) {
    @GetMapping("/list")
    fun getSchedules(
        @CurrentUser userId: UserId,
        @RequestParam("year") year: Int,
        @RequestParam("month") month: Int,
    ): SuccessResponseEntity<ScheduleListResponse> {
        val type = ScheduleType.of(year, month)
        val schedules = scheduleService.fetches(userId, type)
        return ResponseHelper.success(ScheduleListResponse.of(schedules))
    }

    @GetMapping("/{scheduleId}")
    fun getSchedule(
        @CurrentUser userId: UserId,
        @PathVariable("scheduleId") scheduleId: String,
    ): SuccessResponseEntity<ScheduleResponse> {
        val schedule = scheduleService.fetch(userId, ScheduleId.of(scheduleId))
        return ResponseHelper.success(ScheduleResponse.of(schedule))
    }

    @DeleteMapping("")
    fun deleteSchedule(
        @CurrentUser userId: UserId,
        @RequestBody request: ScheduleRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.delete(userId, ScheduleId.of(scheduleId))
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/cancel")
    fun cancelSchedule(
        @CurrentUser userId: UserId,
        @RequestBody request: ScheduleRequest.Cancel,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.cancel(userId, ScheduleId.of(scheduleId))
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun addSchedule(
        @CurrentUser userId: UserId,
        @RequestBody request: ScheduleRequest.Create,
    ): SuccessResponseEntity<ScheduleIdResponse> {
        val scheduleId =
            scheduleService.create(userId, request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds())
        return ResponseHelper.successCreate(ScheduleIdResponse(scheduleId.id))
    }

    @PutMapping("")
    fun updateSchedule(
        @CurrentUser userId: UserId,
        @RequestBody request: ScheduleRequest.Update,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        scheduleService.update(userId, request.toScheduleId(), request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds(), request.toParticipated())
        return ResponseHelper.successOnly()
    }

    @GetMapping("/logs")
    fun getScheduleLogs(
        @CurrentUser userId: UserId,
    ): SuccessResponseEntity<ScheduleLogsResponse> {
        val logs = scheduleService.fetchLogs(userId)
        return ResponseHelper.success(ScheduleLogsResponse.of(logs))
    }
}
