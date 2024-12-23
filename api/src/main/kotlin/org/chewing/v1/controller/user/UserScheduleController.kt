package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.dto.response.schedule.ScheduleIdResponse
import org.chewing.v1.dto.response.schedule.ScheduleListResponse
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.response.SuccessOnlyResponse
import org.chewing.v1.service.user.ScheduleService
import org.chewing.v1.util.helper.ResponseHelper
import org.chewing.v1.util.aliases.SuccessResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/schedule")
class UserScheduleController(
    private val scheduleService: ScheduleService,
) {
    @GetMapping("")
    fun getOwnedSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestParam("year") year: Int,
        @RequestParam("month") month: Int,
    ): SuccessResponseEntity<ScheduleListResponse> {
        val type = ScheduleType.of(year, month)
        val schedules = scheduleService.fetches(userId, type, true)
        return ResponseHelper.success(ScheduleListResponse.of(schedules))
    }

    @GetMapping("/friend/{friendId}")
    fun getFriendSchedule(
        @RequestAttribute("userId") userId: String,
        @PathVariable("friendId") friendId: String,
        @RequestParam("year") year: Int,
        @RequestParam("month") month: Int,
    ): SuccessResponseEntity<ScheduleListResponse> {
        val type = ScheduleType.of(year, month)
        val schedules = scheduleService.fetches(friendId, type, false)
        return ResponseHelper.success(ScheduleListResponse.of(schedules))
    }

    @DeleteMapping("")
    fun deleteSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.delete(scheduleId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun addSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Add,
    ): SuccessResponseEntity<ScheduleIdResponse> {
        val scheduleId = scheduleService.create(userId, request.toScheduleTime(), request.toScheduleContent())
        return ResponseHelper.successCreate(ScheduleIdResponse(scheduleId))
    }
}
