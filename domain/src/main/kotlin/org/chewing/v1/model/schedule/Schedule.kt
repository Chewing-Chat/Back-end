package org.chewing.v1.model.schedule

import java.time.LocalDateTime

class Schedule(
    val id: String,
    val content: ScheduleContent,
    val time: ScheduleTime,
) {
    companion object {
        fun of(
            scheduleId: String,
            title: String,
            memo: String,
            dateTime: LocalDateTime,
            location: String,
            timeDecided: Boolean,
        ): Schedule {
            return Schedule(
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
            )
        }
    }
}
