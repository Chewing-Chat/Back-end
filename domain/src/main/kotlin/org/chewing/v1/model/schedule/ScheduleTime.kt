package org.chewing.v1.model.schedule

import java.time.LocalDateTime

class ScheduleTime private constructor(
    val dateTime: LocalDateTime,
    val timeDecided: Boolean,
) {
    companion object {
        fun of(
            dateTime: LocalDateTime,
            timeDecided: Boolean,
        ): ScheduleTime {
            return ScheduleTime(
                dateTime = dateTime,
                timeDecided = timeDecided,
            )
        }
    }
}
