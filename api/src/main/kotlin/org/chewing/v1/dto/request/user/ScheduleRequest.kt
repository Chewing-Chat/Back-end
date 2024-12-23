package org.chewing.v1.dto.request.user

import org.chewing.v1.model.schedule.ScheduleContent
import org.chewing.v1.model.schedule.ScheduleTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduleRequest {
    data class Delete(
        val scheduleId: String,
    ) {
        fun toScheduleId(): String = scheduleId
    }

    data class Add(
        val title: String,
        val startTime: String,
        val endTime: String,
        val notificationTime: String,
        val memo: String,
        val location: String,
        val private: Boolean,
    ) {
        fun toScheduleContent(): ScheduleContent = ScheduleContent.of(title, memo, location, private)

        fun toScheduleTime(): ScheduleTime {
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val startTime = LocalDateTime.parse(startTime, formatter)
            val endTime = LocalDateTime.parse(endTime, formatter)
            val notification = LocalDateTime.parse(notificationTime, formatter)
            return ScheduleTime.of(startTime, endTime, notification)
        }
    }
}
