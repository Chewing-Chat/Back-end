package org.chewing.v1.controller.user

import org.chewing.v1.dto.request.user.ScheduleRequest
import org.chewing.v1.dto.response.schedule.ScheduleIdResponse
import org.chewing.v1.dto.response.schedule.ScheduleListResponse
import org.chewing.v1.model.schedule.ScheduleType
import org.chewing.v1.model.user.UserId
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
        val schedules = scheduleService.fetches(UserId.of(userId), type)
        return ResponseHelper.success(ScheduleListResponse.of(schedules))
    }

    @DeleteMapping("")
    fun deleteSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Delete,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        val scheduleId = request.toScheduleId()
        scheduleService.delete(UserId.of(userId), scheduleId)
        return ResponseHelper.successOnly()
    }

    @PostMapping("")
    fun addSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Create,
    ): SuccessResponseEntity<ScheduleIdResponse> {
        val scheduleId =
            scheduleService.create(UserId.of(userId), request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds())
        return ResponseHelper.successCreate(ScheduleIdResponse(scheduleId))
    }

    @PutMapping("")
    fun updateSchedule(
        @RequestAttribute("userId") userId: String,
        @RequestBody request: ScheduleRequest.Update,
    ): SuccessResponseEntity<SuccessOnlyResponse> {
        scheduleService.update(UserId.of(userId), request.toScheduleId(), request.toScheduleTime(), request.toScheduleContent(), request.toFriendIds())
        return ResponseHelper.successOnly()
    }
}
