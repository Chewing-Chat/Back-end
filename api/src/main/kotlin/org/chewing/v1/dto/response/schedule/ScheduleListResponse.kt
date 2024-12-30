package org.chewing.v1.dto.response.schedule

import org.chewing.v1.model.schedule.Schedule
import java.time.format.DateTimeFormatter

data class ScheduleListResponse(
    val schedules: List<ScheduleResponse>,
) {
    companion object {
        fun of(schedules: List<Schedule>): ScheduleListResponse {
            return ScheduleListResponse(
                schedules.map {
                    ScheduleResponse.of(it)
                },
            )
        }
    }

    data class ScheduleResponse(
        val scheduleId: String,
        val title: String,
        val dateTime: String,
        val memo: String,
        val location: String,
        val timeDecided: Boolean,
    ) {
        companion object {
            fun of(schedule: Schedule): ScheduleResponse {
                val formatter = DateTimeFormatter.ofPattern("yy-MM-dd HH:mm:ss")
                return ScheduleResponse(
                    schedule.id,
                    schedule.content.title,
                    schedule.time.dateTime.format(formatter),
                    schedule.content.memo,
                    schedule.content.location,
                    schedule.time.timeDecided,
                )
            }
        }
    }
}
