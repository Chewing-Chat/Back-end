package org.chewing.v1.schedule

import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.dto.response.schedule.ScheduleIdResponse
import org.chewing.v1.dto.response.schedule.ScheduleListResponse
import org.chewing.v1.dto.response.schedule.ScheduleLogsResponse
import org.chewing.v1.model.schedule.ScheduleId
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.model.user.UserId
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.chewing.v1.util.helper.ResponseHelper
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestAttribute
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule")
class ScheduleController(
    private val scheduleService: ScheduleService,
) {
    @GetMapping("")
    fun getSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestParam("year") year: Int,
        @RequestParam("month") month: Int,
    ): SuccessResponseEntity<ScheduleListResponse> {
        val type = ScheduleType.of(year, month)
        val schedules = scheduleService.fetches(UserId.of(userId), type)
        return ResponseHelper.success(ScheduleListResponse.of(schedules))
    }

    @DeleteMapping("")
    fun deleteSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.delete(UserId.of(userId), ScheduleId.of(scheduleId))
        return ResponseHelper.successOnly()
    }

    @DeleteMapping("/cancel")
    fun cancelSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Cancel,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.cancel(UserId.of(userId), ScheduleId.of(scheduleId))
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun addSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Create,
    ): SuccessResponseEntity<ScheduleIdResponse> {
        val scheduleId =
            scheduleService.create(UserId.Companion.of(userId), request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds())
        return ResponseHelper.successCreate(ScheduleIdResponse(scheduleId.id))
    }

    @PutMapping("")
    fun updateSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Update,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        scheduleService.update(UserId.of(userId), request.toScheduleId(), request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds(), request.toParticipated())
        return ResponseHelper.successOnly()
    }

    @GetMapping("/logs")
    fun getLogs(
        @RequestAttribute("userId") userId: String,
    ): SuccessResponseEntity<ScheduleLogsResponse> {
        val logs = scheduleService.getLogs(UserId.of(userId))
        return ResponseHelper.success(ScheduleLogsResponse.of(logs))
    }
}
