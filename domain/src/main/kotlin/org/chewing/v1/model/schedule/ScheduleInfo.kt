package org.chewing.v1.model.schedule

import java.time.LocalDateTime

class ScheduleInfo private constructor(
    val scheduleId: ScheduleId,
    val content: ScheduleContent,
    val time: ScheduleTime,
    val status: ScheduleStatus,
) {
    companion object {
        fun of(
            scheduleId: ScheduleId,
            title: String,
            memo: String,
            dateTime: LocalDateTime,
            location: String,
            timeDecided: Boolean,
            status: ScheduleStatus,
        ): ScheduleInfo {
            return ScheduleInfo(
                scheduleId,
                ScheduleContent.of(
                    title,
                    memo,
                    location,
                ),
                ScheduleTime.of(
                    dateTime,
                    timeDecided,
                ),
                status,
            )
        }
    }
}
