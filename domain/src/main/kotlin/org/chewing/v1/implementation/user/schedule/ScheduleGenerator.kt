package org.chewing.v1.implementation.user.schedule

import org.chewing.v1.error.ConflictException
import org.chewing.v1.error.ErrorCode
import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class ScheduleGenerator {
    fun generateScheduleFromString(scheduleStringInfo: String): Pair<ScheduleContent, ScheduleTime> {
        val scheduleData = parseSchedule(scheduleStringInfo)
        val title = scheduleData["title"] ?: ""
        val memo = scheduleData["memo"] ?: ""
        val location = scheduleData["location"] ?: ""
        val startAtStr = scheduleData["startAt"] ?: throw ConflictException(ErrorCode.SCHEDULE_CREATE_FAILED)
        val endAtStr = scheduleData["endAt"] ?: throw ConflictException(ErrorCode.SCHEDULE_CREATE_FAILED)
        val notificationAtStr = scheduleData["notificationAt"] ?: startAtStr
        val startAt = LocalDateTime.parse(startAtStr)
        val endAt = LocalDateTime.parse(endAtStr)
        val notificationAt = LocalDateTime.parse(notificationAtStr)

        val scheduleTime = ScheduleTime.of(startAt, endAt, notificationAt)
        val scheduleContent = ScheduleContent.of(title, memo, location, true)
        return Pair(scheduleContent, scheduleTime)
    }
    private fun parseSchedule(input: String): Map<String, String> {
        return input.lines()
            .map { it.split(": ", limit = 2) }
            .filter { it.size == 2 }
            .associate { it[0].trim() to it[1].trim() }
    }
}
