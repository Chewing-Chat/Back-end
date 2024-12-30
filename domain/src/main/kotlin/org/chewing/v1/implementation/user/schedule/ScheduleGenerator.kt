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
        val dataTimeStr = scheduleData["time"] ?: throw ConflictException(ErrorCode.SCHEDULE_CREATE_FAILED)
        val timeDecided = scheduleData["timeDecided"]?.toBoolean() ?: false
        val dateTime = LocalDateTime.parse(dataTimeStr)
        val scheduleTime = ScheduleTime.of(dateTime, timeDecided)
        val scheduleContent = ScheduleContent.of(title, memo, location)
        return Pair(scheduleContent, scheduleTime)
    }
    private fun parseSchedule(input: String): Map<String, String> {
        return input.lines()
            .map { it.split(": ", limit = 2) }
            .filter { it.size == 2 }
            .associate { it[0].trim() to it[1].trim() }
    }
}
